package recifecultural.dominio.agenda.prereserva;

public class ConfirmarPreReservaOperacao extends OperacaoPreReservaTemplate {

    public ConfirmarPreReservaOperacao(IPreReservaRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    protected void aplicarTransicao(PreReserva preReserva) {
        preReserva.confirmar();
    }
}
