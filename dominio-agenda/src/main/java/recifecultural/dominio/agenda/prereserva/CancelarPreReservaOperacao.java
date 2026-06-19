package recifecultural.dominio.agenda.prereserva;

public class CancelarPreReservaOperacao extends OperacaoPreReservaTemplate {

    public CancelarPreReservaOperacao(IPreReservaRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    protected void aplicarTransicao(PreReserva preReserva) {
        preReserva.cancelar();
    }
}
