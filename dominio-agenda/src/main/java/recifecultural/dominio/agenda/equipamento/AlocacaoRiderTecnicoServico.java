package recifecultural.dominio.agenda.equipamento;

import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de alocação de rider técnico.
 * Coordena a reserva de equipamentos do espaço para atender ao rider de um evento.
 */
public class AlocacaoRiderTecnicoServico {

    private final IEquipamentoRepositorio equipamentoRepositorio;
    private final IEventoRepositorio eventoRepositorio;
    private final INotificacaoServico notificacaoServico;

    public AlocacaoRiderTecnicoServico(IEquipamentoRepositorio equipamentoRepositorio,
                                        IEventoRepositorio eventoRepositorio,
                                        INotificacaoServico notificacaoServico) {
        this.equipamentoRepositorio = equipamentoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.notificacaoServico = notificacaoServico;
    }

    public void alocarEquipamentos(UUID eventoId,
                                    recifecultural.dominio.espaco.espaco.EspacoId espacoId,
                                    String nomeEquipamento,
                                    int quantidadeNecessaria) {
        List<Equipamento> disponiveis = equipamentoRepositorio
                .buscarDisponiveisPorEspacoENome(espacoId, nomeEquipamento, quantidadeNecessaria);

        if (disponiveis.size() < quantidadeNecessaria) {
            throw new IllegalStateException(
                    "Conflito de Infraestrutura: O espaço não possui "
                    + quantidadeNecessaria + " unidades de '" + nomeEquipamento + "' disponíveis.");
        }

        for (Equipamento eq : disponiveis) {
            eq.alocarParaEvento(eventoId);
            equipamentoRepositorio.atualizar(eq);
        }
    }

    public void registrarManutencao(EquipamentoId equipamentoId) {
        Equipamento equipamento = equipamentoRepositorio.obterPorId(equipamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Equipamento não encontrado."));

        UUID eventoAfetadoId = equipamento.getEventoAlocadoId();

        if (eventoAfetadoId != null) {
            eventoRepositorio.obter(eventoAfetadoId).ifPresent(evento -> {
                String mensagem = "ALERTA: O equipamento '" + equipamento.getNome()
                        + "' alocado para o evento '" + evento.getTitulo()
                        + "' foi para manutenção.";
                notificacaoServico.enviarNotificacao(evento.getPromotorId(), mensagem);
            });
        }

        equipamento.enviarParaManutencao();
        equipamentoRepositorio.atualizar(equipamento);
    }

    public void desmobilizarEquipamentosDoEvento(UUID eventoId) {
        List<Equipamento> alocados = equipamentoRepositorio.buscarAlocadosPorEvento(eventoId);
        for (Equipamento eq : alocados) {
            eq.liberar();
            equipamentoRepositorio.atualizar(eq);
        }
    }
}
