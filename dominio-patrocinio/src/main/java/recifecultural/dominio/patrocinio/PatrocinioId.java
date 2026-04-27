package recifecultural.dominio.patrocinio;

import java.util.UUID;

public class PatrocinioId {

    private final UUID valor;

    public PatrocinioId(UUID valor) {
        this.valor = valor;
    }

    public static PatrocinioId novo() {
        return new PatrocinioId(UUID.randomUUID());
    }

    public UUID getValor() { return valor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatrocinioId)) return false;
        return valor.equals(((PatrocinioId) o).valor);
    }

    @Override
    public int hashCode() { return valor.hashCode(); }

    @Override
    public String toString() { return valor.toString(); }
}
