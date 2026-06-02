package recifecultural.dominio.agenda.equipamento;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;
import recifecultural.dominio.espaco.espaco.EspacoId;

public class EquipamentoServico {

    private final IEquipamentoRepositorio repositorio;
    private final INotificacaoServico notificacaoServico;

    public EquipamentoServico(IEquipamentoRepositorio repositorio, INotificacaoServico notificacaoServico) {
        this.repositorio = repositorio;
        this.notificacaoServico = notificacaoServico;
    }

    public EquipamentoId adquirirEquipamento(EspacoId espacoId, String nome) {
        Equipamento novo = new Equipamento(espacoId, nome);
        repositorio.salvar(novo);
        return novo.getId();
    }

    public void reportarManutencao(EquipamentoId id) {
        Equipamento equipamento = repositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));
        equipamento.enviarParaManutencao().ifPresent(evento -> {
            String mensagem = String.format(
                    "Equipamento '%s' enviado para manutenção. Estava alocado ao evento %s.",
                    equipamento.getNome(), evento.getEventoAlocadoId());
            notificacaoServico.enviarBroadcast(mensagem, "EQUIPAMENTO_MANUTENCAO", evento.getEventoAlocadoId());
        });
        repositorio.atualizar(equipamento);
    }

    public void removerEquipamento(EquipamentoId id) {
        Equipamento equipamento = repositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));

        if (equipamento.getStatus() == StatusEquipamento.ALOCADO) {
            throw new IllegalStateException("Não é possível remover um equipamento que está alocado a um evento ativo.");
        }

        repositorio.deletar(id);
    }

    public void liberar(EquipamentoId id) {
        Equipamento equipamento = repositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));
        equipamento.liberar();
        repositorio.atualizar(equipamento);
    }
}
