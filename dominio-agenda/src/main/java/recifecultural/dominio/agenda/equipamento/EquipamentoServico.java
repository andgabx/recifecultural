package recifecultural.dominio.agenda.equipamento;

import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;
import recifecultural.dominio.espaco.espaco.EspacoId;

public class EquipamentoServico {

    private final IEquipamentoRepositorio repositorio;
    private final INotificacaoServico notificacaoServico;
    private final IEventoRepositorio eventoRepositorio;

    public EquipamentoServico(IEquipamentoRepositorio repositorio,
                               INotificacaoServico notificacaoServico,
                               IEventoRepositorio eventoRepositorio) {
        this.repositorio = repositorio;
        this.notificacaoServico = notificacaoServico;
        this.eventoRepositorio = eventoRepositorio;
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
            java.util.UUID eventoAlocadoId = evento.getEventoAlocadoId();
            eventoRepositorio.obter(eventoAlocadoId).ifPresent(eventoCarregado -> {
                String mensagem = String.format(
                        "ALERTA: O equipamento '%s' alocado para o evento '%s' foi para manutenção.",
                        equipamento.getNome(), eventoCarregado.getTitulo());
                notificacaoServico.enviarNotificacao(eventoCarregado.getPromotorId(), mensagem);
            });
        });
        repositorio.atualizar(equipamento);
    }

    public void removerEquipamento(EquipamentoId id) {
        Equipamento equipamento = repositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));

        boolean estaAlocado = equipamento.getStatus() == StatusEquipamento.ALOCADO;
        java.util.UUID eventoAlocadoId = equipamento.getEventoAlocadoId();

        repositorio.deletar(id);

        if (estaAlocado && eventoAlocadoId != null) {
            eventoRepositorio.obter(eventoAlocadoId).ifPresent(eventoCarregado -> {
                String mensagem = String.format(
                        "O equipamento '%s' alocado para o evento '%s' foi removido do inventário pelo gestor.",
                        equipamento.getNome(), eventoCarregado.getTitulo());
                notificacaoServico.enviarNotificacao(eventoCarregado.getPromotorId(), mensagem);
            });
        }
    }

    public void liberar(EquipamentoId id) {
        Equipamento equipamento = repositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));

        boolean estaAlocado = equipamento.getStatus() == StatusEquipamento.ALOCADO;
        java.util.UUID eventoAlocadoId = equipamento.getEventoAlocadoId();

        equipamento.liberar();
        repositorio.atualizar(equipamento);

        if (estaAlocado && eventoAlocadoId != null) {
            eventoRepositorio.obter(eventoAlocadoId).ifPresent(eventoCarregado -> {
                String mensagem = String.format(
                        "O equipamento '%s' alocado para o evento '%s' foi liberado pelo gestor.",
                        equipamento.getNome(), eventoCarregado.getTitulo());
                notificacaoServico.enviarNotificacao(eventoCarregado.getPromotorId(), mensagem);
            });
        }
    }
}
