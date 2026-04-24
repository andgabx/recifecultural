package recifecultural.dominio.agenda.equipamento;


import recifecultural.dominio.agenda.espaco.EspacoId;

public class EquipamentoServico {

    private final IEquipamentoRepositorio repositorio;

    public EquipamentoServico(IEquipamentoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public EquipamentoId adquirirEquipamento(EspacoId espacoId, String nome) {
        Equipamento novo = new Equipamento(espacoId, nome);
        repositorio.salvar(novo);
        return novo.getId();
    }

    public void removerEquipamento(EquipamentoId id) {
        Equipamento equipamento = repositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));

        if (equipamento.getStatus() == recifecultural.dominio.agenda.equipamento.StatusEquipamento.ALOCADO) {
            throw new IllegalStateException("Não é possível remover um equipamento que está alocado a um evento ativo.");
        }

        repositorio.deletar(id);
    }
}
