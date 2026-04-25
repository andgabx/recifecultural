package recifecultural.dominio.artista.artista;

import java.util.UUID;

public record ArtistaId(UUID valor) {
    public ArtistaId {
        if (valor == null) throw new IllegalArgumentException("ID do artista não pode ser nulo.");
    }
    public static ArtistaId novo() { return new ArtistaId(UUID.randomUUID()); }
    public static ArtistaId de(String valor) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("ID em texto não pode ser vazio.");
        return new ArtistaId(UUID.fromString(valor));
    }
}