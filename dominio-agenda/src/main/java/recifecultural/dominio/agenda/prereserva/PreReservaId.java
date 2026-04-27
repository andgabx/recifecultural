package recifecultural.dominio.agenda.prereserva;

import java.util.UUID;

public record PreReservaId(UUID valor) {
    public PreReservaId {
        if (valor == null) throw new IllegalArgumentException("ID da pré-reserva não pode ser nulo.");
    }
    public static PreReservaId novo() { return new PreReservaId(UUID.randomUUID()); }
}