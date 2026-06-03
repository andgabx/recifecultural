package recifecultural.aplicacao.agenda.evento;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.FeedbackReprovacao;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class EventoServicoAplicacao {

    private final EventoServico servico;
    private final EventoRepositorioAplicacao repositorio;

    public EventoServicoAplicacao(EventoServico servico, EventoRepositorioAplicacao repositorio) {
        notNull(servico, "EventoServico não pode ser nulo.");
        notNull(repositorio, "EventoRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
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

        servico.salvar(evento);
        return evento.getId();
    }

    public void editar(UUID id, EditarEventoComando cmd) {
        notNull(id, "id obrigatório.");
        notNull(cmd, "Comando obrigatório.");
        notBlank(cmd.titulo(), "Título obrigatório.");

        Periodo periodo = derivarPeriodo(cmd.periodoInicio(), cmd.periodoFim(), cmd.datasApresentacao());

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
    }

    public void reprovar(UUID id, FeedbackReprovacao feedback) {
        servico.reprovar(id, feedback);
    }

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
            List<LocalDateTime> datasApresentacao
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
            List<LocalDateTime> datasApresentacao
    ) {}
}
