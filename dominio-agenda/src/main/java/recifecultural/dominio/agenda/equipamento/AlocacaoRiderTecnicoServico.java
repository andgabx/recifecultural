package recifecultural.dominio.agenda.equipamento;

import recifecultural.dominio.agenda.espaco.EspacoId;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.notificacao.NotificacaoServico;

import java.util.List;
import java.util.UUID;

public class AlocacaoRiderTecnicoServico {

    private final IEquipamentoRepositorio equipamentoRepositorio;
    private final IEventoRepositorio eventoRepositorio;
    private final NotificacaoServico notificacaoServico; // Injeção do novo serviço

    public AlocacaoRiderTecnicoServico(
            IEquipamentoRepositorio equipamentoRepositorio,
            IEventoRepositorio eventoRepositorio,
            NotificacaoServico notificacaoServico) {
        this.equipamentoRepositorio = equipamentoRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.notificacaoServico = notificacaoServico;
    }

    public void alocarEquipamentos(UUID eventoId, EspacoId espacoId, String nomeEquipamento, int quantidadeNecessaria) {
        List<Equipamento> disponiveis = equipamentoRepositorio.buscarDisponiveisPorEspacoENome(espacoId, nomeEquipamento, quantidadeNecessaria);

        if (disponiveis.size() < quantidadeNecessaria) {
            throw new IllegalStateException("Conflito de Infraestrutura: O espaço não possui " + quantidadeNecessaria + " unidades de '" + nomeEquipamento + "' disponíveis.");
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
            Evento eventoAfetado = eventoRepositorio.obter(eventoAfetadoId)
                    .orElseThrow(() -> new IllegalStateException("Evento vinculado não encontrado."));

            // Lógica de notificação acionada aqui
            String mensagemAlerta = "ALERTA CRÍTICO: O equipamento '" + equipamento.getNome() +
                    "' alocado para o evento '" + eventoAfetado.getTitulo() +
                    "' precisou de ir para manutenção. Por favor, providencie infraestrutura externa.";

            notificacaoServico.enviarNotificacao(eventoAfetado.getPromotorId(), mensagemAlerta);
        }

        equipamento.enviarParaManutencao();
        equipamentoRepositorio.atualizar(equipamento);
    }

    public void desmobilizarEquipamentosDoEvento(UUID eventoId) {
        List<Equipamento> equipamentosAlocados = equipamentoRepositorio.buscarAlocadosPorEvento(eventoId);

        for (Equipamento eq : equipamentosAlocados) {
            eq.liberar();
            equipamentoRepositorio.atualizar(eq);
        }
    }
}