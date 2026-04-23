package recifecultural.dominio.ingressos;

import java.util.Objects;
import java.util.UUID;

public final class IngressoId {

    private final UUID id;

    public IngressoId(UUID id) {
        if (id == null) throw new IllegalArgumentException("O id do ingresso não pode ser nulo.");
        this.id = id;
    }

    public static IngressoId novo() {
        return new IngressoId(UUID.randomUUID());
    }

    public UUID valor() {
        return id;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof IngressoId)) return false;
        return id.equals(((IngressoId) outro).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
