package recifecultural.dominio.artista.produtor;

import java.util.UUID;

public record ProdutorId(UUID valor) {
    public ProdutorId {
        if (valor == null) throw new IllegalArgumentException("ID do produtor não pode ser nulo.");
    }
    public static ProdutorId novo() { return new ProdutorId(UUID.randomUUID()); }
    public static ProdutorId de(String valor) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("ID em texto não pode ser vazio.");
        return new ProdutorId(UUID.fromString(valor));
    }
}