package recifecultural.dominio.agenda.setor;

import java.util.UUID;

public record SetorId(UUID valor) {
    public SetorId {
        if (valor == null) throw new IllegalArgumentException("ID do setor não pode ser nulo.");
    }
    public static SetorId novo() { return new SetorId(UUID.randomUUID()); }
    public static SetorId de(String valor) {
        if (valor == null || valor.isBlank()) throw new IllegalArgumentException("ID em texto não pode ser vazio.");
        return new SetorId(UUID.fromString(valor));
    }
}