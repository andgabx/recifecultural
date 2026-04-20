package recifecultural.dominio.artistas;

import java.util.UUID;

public final class ArtistId {

    private final UUID id;

    public ArtistId(UUID id) {
        if (id == null) throw new IllegalArgumentException("ID do artista é obrigatório.");
        this.id = id;
    }

    public static ArtistId novo() {
        return new ArtistId(UUID.randomUUID());
    }

    public UUID valor() {
        return id;
    }

    @Override
    public boolean equals(Object outro) {
        if (this == outro) return true;
        if (!(outro instanceof ArtistId outroId)) return false;
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
