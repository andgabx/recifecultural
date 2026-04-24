package recifecultural.dominio.agenda.equipamento;

import java.util.UUID;

public record EquipamentoId(UUID valor) {
    public EquipamentoId {
        if (valor == null) throw new IllegalArgumentException("O ID do equipamento não pode ser nulo.");
    }
    public static EquipamentoId novo() {
        return new EquipamentoId(UUID.randomUUID());
    }
}