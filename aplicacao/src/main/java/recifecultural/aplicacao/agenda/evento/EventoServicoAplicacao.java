package recifecultural.aplicacao.agenda.evento;

import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;
import recifecultural.dominio.agenda.equipamento.AlocacaoRiderTecnicoServico;
import recifecultural.dominio.agenda.equipamento.RiderItem;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.FeedbackReprovacao;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class EventoServicoAplicacao {

    private final EventoServico servico;
    private final EventoRepositorioAplicacao repositorio;
    private final IBloqueioAdministrativoRepositorio bloqueioRepositorio;
    private final AlocacaoRiderTecnicoServico alocacaoRiderServico;

    public EventoServicoAplicacao(EventoServico servico,
                                   EventoRepositorioAplicacao repositorio,
                                   IBloqueioAdministrativoRepositorio bloqueioRepositorio,
                                   AlocacaoRiderTecnicoServico alocacaoRiderServico) {
        notNull(servico, "EventoServico não pode ser nulo.");
        notNull(repositorio, "EventoRepositorioAplicacao não pode ser nulo.");
        notNull(bloqueioRepositorio, "IBloqueioAdministrativoRepositorio não pode ser nulo.");
        notNull(alocacaoRiderServico, "AlocacaoRiderTecnicoServico não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
        this.bloqueioRepositorio = bloqueioRepositorio;
        this.alocacaoRiderServico = alocacaoRiderServico;
    }

    public List<EventoResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    public List<EventoResumo> listarPorPromotor(UUID promotorId) {
        notNull(promotorId, "O id do promotor é obrigatório.");
        return repositorio.listarPorPromotor(promotorId);
    }

    public EventoResumoExpandido buscarResumoExpandido(UUID id) {
        return repositorio.buscarResumoExpandido(id);
    }

    public UUID criar(CriarEventoComando cmd) {
        notNull(cmd, "Comando obrigatório.");
        notNull(cmd.promotorId(), "promotorId obrigatório.");
        notBlank(cmd.titulo(), "Título obrigatório.");

        Periodo periodo = derivarPeriodo(cmd.periodoInicio(), cmd.periodoFim(), cmd.datasApresentacao());
        if (cmd.localId() != null) {
            verificarBloqueioAtivo(cmd.localId(), periodo);
        }

        Preco preco = null;
        if (cmd.precoInteira() != null) {
            preco = new Preco(cmd.precoInteira(), cmd.precoMeia(), null);
        }

        Evento evento = new Evento(
                UUID.randomUUID(),
                cmd.promotorId(),
                cmd.localId(),
                cmd.titulo(),
                cmd.descricaoCurta(),
                cmd.descricaoLonga(),
                periodo,
                null,
                preco
        );

        if (cmd.categoria() != null && !cmd.categoria().isBlank()) {
            evento.definirCategoria(cmd.categoria());
        }
        if (cmd.artistas() != null) {
            cmd.artistas().forEach(evento::adicionarArtista);
        }
        if (cmd.datasApresentacao() != null) {
            cmd.datasApresentacao().forEach(evento::programarApresentacao);
        }
        if (cmd.riderItems() != null) {
            cmd.riderItems().forEach(item ->
                    evento.adicionarRiderItem(item.equipamentoId(), item.quantidade()));
        }

        servico.salvar(evento);
        return evento.getId();
    }

    @Transactional
    public void editar(UUID id, EditarEventoComando cmd) {
        notNull(id, "id obrigatório.");
        notNull(cmd, "Comando obrigatório.");
        notBlank(cmd.titulo(), "Título obrigatório.");

        Periodo periodo = derivarPeriodo(cmd.periodoInicio(), cmd.periodoFim(), cmd.datasApresentacao());
        if (cmd.localId() != null) {
            verificarBloqueioAtivo(cmd.localId(), periodo);
        }

        Preco preco = null;
        if (cmd.precoInteira() != null) {
            preco = new Preco(cmd.precoInteira(), cmd.precoMeia(), null);
        }
        servico.editar(
                id,
                cmd.titulo(),
                cmd.descricaoCurta(),
                cmd.descricaoLonga(),
                periodo,
                preco,
                cmd.categoria(),
                cmd.localId(),
                cmd.artistas(),
                cmd.datasApresentacao()
        );

        if (cmd.riderItems() != null) {
            List<RiderItem> novosItens = cmd.riderItems().stream()
                    .map(item -> new RiderItem(item.equipamentoId(), item.quantidade()))
                    .toList();
            servico.editarRider(id, novosItens);
        }
    }

    private Periodo derivarPeriodo(LocalDateTime periodoInicio, LocalDateTime periodoFim,
                                    List<LocalDateTime> datasApresentacao) {
        LocalDateTime inicio = periodoInicio;
        LocalDateTime fim = periodoFim;
        if (inicio == null && fim == null) {
            if (datasApresentacao == null || datasApresentacao.isEmpty())
                throw new IllegalArgumentException("Informe o período ou ao menos uma data de apresentação.");
            inicio = datasApresentacao.get(0);
            fim = datasApresentacao.get(0);
        } else {
            notNull(inicio, "Período de início obrigatório.");
            notNull(fim, "Período de fim obrigatório.");
        }
        return new Periodo(inicio, fim);
    }

    public void submeterParaAnalise(UUID id) {
        servico.submeterParaAnalise(id);
    }

    public void aprovar(UUID id) {
        servico.aprovar(id);

        servico.obter(id).ifPresent(evento -> {
            if (evento.getLocalId() != null) {
                EspacoId espacoId = new EspacoId(evento.getLocalId());
                LocalDate inicio = evento.getPeriodo().getInicio().toLocalDate();
                LocalDate fim = evento.getPeriodo().getFim().toLocalDate();
                for (RiderItem item : evento.getRiderItems()) {
                    alocacaoRiderServico.alocarEquipamentoPorId(
                            id,
                            espacoId,
                            item.getEquipamentoId(),
                            item.getQuantidade(),
                            inicio,
                            fim
                    );
                }
            }
        });
    }

    public void reprovar(UUID id, FeedbackReprovacao feedback) {
        servico.reprovar(id, feedback);
    }

    public void cancelar(UUID id) {
        alocacaoRiderServico.desmobilizarEquipamentosDoEvento(id);
        servico.cancelar(id, "Cancelado via aplicação.");
    }

    /**
     * Rejeita a criação/edição de um evento cujo período se sobreponha
     * a um BloqueioAdministrativo ativo no mesmo espaço.
     * Sobreposição: bloqueio.inicio <= evento.fim AND bloqueio.fim >= evento.inicio
     */
    private void verificarBloqueioAtivo(UUID localId, Periodo periodo) {
        EspacoId espacoId = new EspacoId(localId);
        List<BloqueioAdministrativo> bloqueios = bloqueioRepositorio.buscarPorEspaco(espacoId);
        boolean bloqueado = bloqueios.stream()
                .filter(BloqueioAdministrativo::isAtivo)
                .anyMatch(b -> {
                    LocalDateTime bloqueioInicio = b.getDataInicio().atStartOfDay();
                    LocalDateTime bloqueioFim    = b.getDataFim().atTime(23, 59, 59);
                    return !periodo.getFim().isBefore(bloqueioInicio)
                            && !periodo.getInicio().isAfter(bloqueioFim);
                });
        if (bloqueado) {
            throw new IllegalStateException(
                    "O espaço possui um bloqueio administrativo ativo que cobre o período informado. " +
                    "Escolha datas fora do período de bloqueio.");
        }
    }

    public record RiderItemComando(
            java.util.UUID equipamentoId,
            int quantidade
    ) {}

    public record CriarEventoComando(
            UUID promotorId,
            UUID localId,
            String titulo,
            String descricaoCurta,
            String descricaoLonga,
            LocalDateTime periodoInicio,
            LocalDateTime periodoFim,
            String categoria,
            BigDecimal precoInteira,
            BigDecimal precoMeia,
            List<UUID> artistas,
            List<LocalDateTime> datasApresentacao,
            List<RiderItemComando> riderItems
    ) {}

    public record EditarEventoComando(
            UUID localId,
            String titulo,
            String descricaoCurta,
            String descricaoLonga,
            LocalDateTime periodoInicio,
            LocalDateTime periodoFim,
            String categoria,
            BigDecimal precoInteira,
            BigDecimal precoMeia,
            List<UUID> artistas,
            List<LocalDateTime> datasApresentacao,
            List<RiderItemComando> riderItems
    ) {}
}
