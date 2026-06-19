package recifecultural.dominio.agenda.prereserva;

public class ExpirarPreReservaOperacao extends OperacaoPreReservaTemplate {

    public ExpirarPreReservaOperacao(IPreReservaRepositorio repositorio) {
        super(repositorio);
    }

    @Override
    protected void aplicarTransicao(PreReserva preReserva) {
        preReserva.expirar(agora());
    }
}
