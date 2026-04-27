package recifecultural.dominio.agenda.prereserva;

/**
 * Lançada quando dois usuários tentam pré-reservar o mesmo assento simultaneamente.
 * A infraestrutura captura OptimisticLockException do JPA e relança como esta exceção.
 * A camada de apresentação a mapeia para HTTP 409 Conflict.
 */
public class ConcorrenciaException extends RuntimeException {
    public ConcorrenciaException(String codigoAssento) {
        super("O assento " + codigoAssento + " foi reservado por outro usuário. Tente outro assento.");
    }
}