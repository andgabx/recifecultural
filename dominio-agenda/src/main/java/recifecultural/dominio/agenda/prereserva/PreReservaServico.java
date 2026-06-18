package recifecultural.dominio.agenda.prereserva;

import recifecultural.dominio.espaco.setor.ISetorRepositorio;
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


    public PreReservaId reservar(UUID setorId, UUID assentoId, UUID usuarioId, UUID eventoId,
                                 DuracaoPreReserva duracao) {
        LocalDateTime agora = LocalDateTime.now();

        List<PreReserva> ativas = preReservaRepositorio.listarAtivasPorAssentoEEvento(assentoId, eventoId);
        boolean haPreReservaAtiva = ativas.stream().anyMatch(pr -> !pr.estaExpirada(agora));
        if (haPreReservaAtiva)
            throw new IllegalStateException("Assento já possui pré-reserva ativa.");

        PreReserva preReserva = new PreReserva(assentoId, setorId, usuarioId, eventoId, duracao, agora);
        preReservaRepositorio.salvar(preReserva);
        return preReserva.getId();
    }

    public void cancelar(PreReservaId preReservaId) {
        PreReserva preReserva = preReservaRepositorio.obterPorId(preReservaId)
                .orElseThrow(() -> new IllegalArgumentException("Pré-reserva não encontrada."));

        preReserva.cancelar();
        preReservaRepositorio.atualizar(preReserva);
    }

    public void confirmar(PreReservaId preReservaId) {
        PreReserva preReserva = preReservaRepositorio.obterPorId(preReservaId)
                .orElseThrow(() -> new IllegalArgumentException("Pré-reserva não encontrada."));

        preReserva.confirmar();
        preReservaRepositorio.atualizar(preReserva);
    }

    public List<PreReserva> listarAtivasPorEvento(UUID eventoId) {
        return preReservaRepositorio.listarAtivasPorEvento(eventoId);
    }

    public void expirarVencidas() {
        LocalDateTime agora = LocalDateTime.now();
        List<PreReserva> vencidas = preReservaRepositorio.listarAtivasExpiradas(agora);
        for (PreReserva pr : vencidas) {
            pr.expirar(agora);
            preReservaRepositorio.atualizar(pr);
        }
    }
}
