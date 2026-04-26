package recifecultural.dominio.agenda.espaco;

import java.util.UUID;


public final class EspacoId {

    private final UUID id;

    public EspacoId(UUID id) {
        if (id == null) throw new IllegalArgumentException("ID do espaço é obrigatório.");
        this.id = id;
    }

    public static EspacoId novo() {
        return new EspacoId(UUID.randomUUID());
    }

    public UUID valor() {
        return id;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof EspacoId outroId)) return false;
        return id.equals(outroId.id);
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
