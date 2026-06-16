package recifecultural.dominio.agenda.equipamento;

import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.time.LocalDate;
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
                                    int quantidadeNecessaria,
                                    LocalDate inicio,
                                    LocalDate fim) {
        List<Equipamento> disponiveis = equipamentoRepositorio
                .buscarDisponiveisPorEspacoENome(espacoId, nomeEquipamento, quantidadeNecessaria, inicio, fim);

        int count = disponiveis.size();

        if (count < quantidadeNecessaria) {
            String mensagem = "Equipamento '" + nomeEquipamento + "' indisponível para o evento "
                    + eventoId + ": solicitado " + quantidadeNecessaria + ", disponível " + count + ".";
            notificacaoServico.enviarBroadcast(mensagem);
        }

        for (Equipamento eq : disponiveis) {
            eq.alocarParaEvento(eventoId, inicio, fim);
            equipamentoRepositorio.atualizar(eq);
        }
    }

    public boolean verificarDisponibilidade(recifecultural.dominio.espaco.espaco.EspacoId espacoId,
                                             String nomeEquipamento,
                                             int quantidade,
                                             LocalDate inicio,
                                             LocalDate fim) {
        List<Equipamento> disponiveis;
        if (inicio != null && fim != null) {
            disponiveis = equipamentoRepositorio
                    .buscarDisponiveisPorEspacoENome(espacoId, nomeEquipamento, quantidade, inicio, fim);
        } else {
            disponiveis = equipamentoRepositorio
                    .buscarDisponiveisPorEspacoENomeSemData(espacoId, nomeEquipamento, quantidade);
        }
        return disponiveis.size() >= quantidade;
    }

    public void desmobilizarEquipamentosDoEvento(UUID eventoId) {
        List<Equipamento> alocados = equipamentoRepositorio.buscarAlocadosPorEvento(eventoId);
        for (Equipamento eq : alocados) {
            eq.liberar();
            equipamentoRepositorio.atualizar(eq);
        }
    }
}
