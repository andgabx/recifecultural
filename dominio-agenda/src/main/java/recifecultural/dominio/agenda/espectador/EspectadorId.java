package recifecultural.dominio.agenda.espectador;

import java.util.Objects;
import java.util.UUID;

public record EspectadorId(UUID valor) {

    public EspectadorId {
        Objects.requireNonNull(valor, "ID do espectador é obrigatório.");
    }

    public static EspectadorId gerar() {
        return new EspectadorId(UUID.randomUUID());
    }
}
