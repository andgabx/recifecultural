package recifecultural.dominio.agenda.prereserva;

import recifecultural.dominio.agenda.setor.ISetorRepositorio;
import recifecultural.dominio.agenda.setor.Setor;
import recifecultural.dominio.agenda.setor.SetorId;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class PreReservaServico {

    private final IPreReservaRepositorio preReservaRepositorio;
    private final ISetorRepositorio setorRepositorio;

    public PreReservaServico(IPreReservaRepositorio preReservaRepositorio,
                             ISetorRepositorio setorRepositorio) {
        if (preReservaRepositorio == null) throw new IllegalArgumentException("Repositório de pré-reservas é obrigatório.");
        if (setorRepositorio == null) throw new IllegalArgumentException("Repositório de setores é obrigatório.");
        this.preReservaRepositorio = preReservaRepositorio;
        this.setorRepositorio = setorRepositorio;
    }

    /**
     * Tenta pré-reservar um assento.
     * O controle de concorrência é garantido pela combinação:
     *   1. verificação de pré-reservas ativas no domínio (guard rápido)
     *   2. @Version no AssentoJpaEntity (optimistic locking no JPA)
     *
     * Se dois threads passam pela verificação simultaneamente, o JPA
     * garante que apenas um commit terá sucesso. A infraestrutura captura
     * OptimisticLockException e relança como ConcorrenciaException.
     */
    public PreReservaId reservar(UUID setorId, UUID assentoId, UUID usuarioId,
                                 DuracaoPreReserva duracao) {
        LocalDateTime agora = LocalDateTime.now();

        List<PreReserva> ativas = preReservaRepositorio.listarAtivasPorAssento(assentoId);
        boolean haPreReservaAtiva = ativas.stream().anyMatch(pr -> !pr.estaExpirada(agora));
        if (haPreReservaAtiva)
            throw new IllegalStateException("Assento já possui pré-reserva ativa.");

        Setor setor = setorRepositorio.obterPorId(SetorId.de(setorId.toString()))
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado."));

        // marcarPreReservado lança IllegalStateException se assento não LIVRE
        setor.preReservar(assentoId);
        setorRepositorio.atualizar(setor);
        // ^ aqui o JPA pode lançar OptimisticLockException → infraestrutura relança como ConcorrenciaException

        PreReserva preReserva = new PreReserva(assentoId, setorId, usuarioId, duracao, agora);
        preReservaRepositorio.salvar(preReserva);
        return preReserva.getId();
    }

    public void cancelar(PreReservaId preReservaId) {
        PreReserva preReserva = preReservaRepositorio.obterPorId(preReservaId)
                .orElseThrow(() -> new IllegalArgumentException("Pré-reserva não encontrada."));

        preReserva.cancelar();
        preReservaRepositorio.atualizar(preReserva);

        Setor setor = setorRepositorio.obterPorId(SetorId.de(preReserva.getSetorId().toString()))
                .orElseThrow(() -> new IllegalArgumentException("Setor não encontrado."));
        setor.liberarAssento(preReserva.getAssentoId());
        setorRepositorio.atualizar(setor);
    }

    public void expirarVencidas() {
        LocalDateTime agora = LocalDateTime.now();
        List<PreReserva> vencidas = preReservaRepositorio.listarAtivasExpiradas(agora);
        for (PreReserva pr : vencidas) {
            pr.expirar(agora);
            preReservaRepositorio.atualizar(pr);

            setorRepositorio.obterPorId(SetorId.de(pr.getSetorId().toString()))
                    .ifPresent(setor -> {
                        setor.liberarAssento(pr.getAssentoId());
                        setorRepositorio.atualizar(setor);
                    });
        }
    }
}