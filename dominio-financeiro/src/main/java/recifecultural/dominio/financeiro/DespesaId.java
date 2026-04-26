package recifecultural.dominio.financeiro;

import java.util.UUID;

public final class DespesaId {

    private final UUID id;

    public DespesaId(UUID id) {
        if (id == null) throw new IllegalArgumentException("O id da despesa não pode ser nulo.");
        this.id = id;
    }

    public static DespesaId novo() {
        return new DespesaId(UUID.randomUUID());
    }

    public UUID valor() {
        return id;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof DespesaId)) return false;
        return id.equals(((DespesaId) outro).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
