package recifecultural.dominio.agenda.equipamento;

public final class RiderItem {
    private final String nomeEquipamento;
    private final int quantidade;

    public RiderItem(String nomeEquipamento, int quantidade) {
        if (nomeEquipamento == null || nomeEquipamento.isBlank())
            throw new IllegalArgumentException("Nome do equipamento é obrigatório.");
        if (quantidade < 1)
            throw new IllegalArgumentException("Quantidade deve ser pelo menos 1.");
        this.nomeEquipamento = nomeEquipamento;
        this.quantidade = quantidade;
    }

    public String getNomeEquipamento() {
        return nomeEquipamento;
    }

    public int getQuantidade() {
        return quantidade;
    }
}
