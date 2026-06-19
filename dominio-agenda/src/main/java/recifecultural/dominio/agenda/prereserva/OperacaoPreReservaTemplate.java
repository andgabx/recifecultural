package recifecultural.dominio.agenda.prereserva;

import java.time.LocalDateTime;

public abstract class OperacaoPreReservaTemplate {

    private final IPreReservaRepositorio repositorio;

    protected OperacaoPreReservaTemplate(IPreReservaRepositorio repositorio) {
        if (repositorio == null) throw new IllegalArgumentException("Repositório de pré-reservas é obrigatório.");
        this.repositorio = repositorio;
    }

    // Esqueleto fixo por ID
    public final void executar(PreReservaId id) {
        PreReserva preReserva = buscar(id);
        aplicarTransicao(preReserva);
        persistir(preReserva);
        notificar(preReserva);
    }

    // Esqueleto fixo com objeto já carregado (evita re-busca desnecessária)
    public final void executar(PreReserva preReserva) {
        aplicarTransicao(preReserva);
        persistir(preReserva);
        notificar(preReserva);
    }

    // Passo variável — cada operação define sua transição de estado
    protected abstract void aplicarTransicao(PreReserva preReserva);

    // Passos fixos — comportamento padrão compartilhado
    private PreReserva buscar(PreReservaId id) {
        return repositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Pré-reserva não encontrada: " + id.valor()));
    }

    private void persistir(PreReserva preReserva) {
        repositorio.atualizar(preReserva);
    }

    // Notificação é vazio por padrão — subclasses podem sobrescrever
    protected void notificar(PreReserva preReserva) {
    }

    protected LocalDateTime agora() {
        return LocalDateTime.now();
    }
}
