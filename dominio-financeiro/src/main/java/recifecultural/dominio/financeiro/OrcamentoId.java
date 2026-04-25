package recifecultural.dominio.financeiro;

import java.util.Objects;
import java.util.UUID;

public final class OrcamentoId {

    private final UUID id;

    public OrcamentoId(UUID id) {
        if (id == null) throw new IllegalArgumentException("O id do orçamento não pode ser nulo.");
        this.id = id;
    }

    public static OrcamentoId novo() {
        return new OrcamentoId(UUID.randomUUID());
    }

    public UUID valor() {
        return id;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof OrcamentoId)) return false;
        return id.equals(((OrcamentoId) outro).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
