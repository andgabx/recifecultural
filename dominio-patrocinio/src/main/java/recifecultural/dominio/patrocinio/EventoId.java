package recifecultural.dominio.patrocinio;

import java.util.UUID;

public class EventoId {

    private final UUID valor;

    public EventoId(UUID valor) {
        this.valor = valor;
    }

    public static EventoId novo() {
        return new EventoId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventoId)) return false;
        return valor.equals(((EventoId) o).valor);
    }

    @Override
    public int hashCode() { return valor.hashCode(); }

    @Override
    public String toString() { return valor.toString(); }
}
