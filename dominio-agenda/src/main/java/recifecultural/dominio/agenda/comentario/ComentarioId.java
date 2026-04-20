package recifecultural.dominio.agenda.comentario;

import java.util.Objects;
import java.util.UUID;

public record ComentarioId(UUID valor) {

    public ComentarioId {
        Objects.requireNonNull(valor, "ID do comentário é obrigatório.");
    }

    public static ComentarioId gerar() {
        return new ComentarioId(UUID.randomUUID());
    }
}
