package recifecultural.dominio.agenda.equipamento;

import java.util.UUID;

public final class RiderItem {
    private final UUID equipamentoId;
    private final int quantidade;

    public RiderItem(UUID equipamentoId, int quantidade) {
        if (equipamentoId == null)
            throw new IllegalArgumentException("Id do equipamento é obrigatório.");
        if (quantidade < 1)
            throw new IllegalArgumentException("Quantidade deve ser pelo menos 1.");
        this.equipamentoId = equipamentoId;
        this.quantidade = quantidade;
    }

    public UUID getEquipamentoId() {
        return equipamentoId;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
