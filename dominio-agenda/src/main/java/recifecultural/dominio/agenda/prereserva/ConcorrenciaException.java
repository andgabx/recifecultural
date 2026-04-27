package recifecultural.dominio.agenda.prereserva;

public class ConcorrenciaException extends RuntimeException {
    public ConcorrenciaException(String codigoAssento) {
        super("O assento " + codigoAssento + " foi reservado por outro usuário. Tente outro assento.");
    }
}