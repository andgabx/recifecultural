package recifecultural.dominio.agenda.evento;

import java.util.Objects;
import java.util.UUID;

public record EventoId(UUID valor) {

    public EventoId {
        Objects.requireNonNull(valor, "ID do evento é obrigatório.");
    }

    public static EventoId gerar() {
        return new EventoId(UUID.randomUUID());
    }
}
