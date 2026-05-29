package recifecultural.apresentacao.bff.evento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.agenda.evento.FeedbackReprovacao;
import recifecultural.aplicacao.agenda.evento.EventoResumo;
import recifecultural.aplicacao.agenda.evento.EventoResumoExpandido;
import recifecultural.aplicacao.agenda.evento.EventoServicoAplicacao;
import recifecultural.aplicacao.agenda.evento.EventoServicoAplicacao.CriarEventoComando;
import recifecultural.aplicacao.agenda.evento.EventoServicoAplicacao.EditarEventoComando;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Eventos")
@RestController
@RequestMapping("/api/bff/eventos")
public class EventoBffControlador extends AbstractBffControlador {

    private final EventoServicoAplicacao servico;

    public EventoBffControlador(EventoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista eventos")
    @GetMapping
    public ResponseEntity<List<EventoResumo>> listar() {
        return responder(servico.pesquisarResumos());
    }

    @Operation(summary = "Lista eventos de um produtor")
    @GetMapping("/produtor/{promotorId}")
    public ResponseEntity<List<EventoResumo>> listarPorProdutor(@PathVariable UUID promotorId) {
        return responder(servico.listarPorPromotor(promotorId));
    }

    @Operation(summary = "Busca evento expandido")
    @GetMapping("/{id}")
    public ResponseEntity<EventoResumoExpandido> buscar(@PathVariable UUID id) {
        return responder(servico.buscarResumoExpandido(id));
    }

    @Operation(summary = "Cria evento em rascunho")
    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@RequestBody CriarEventoRequisicao req) {
        UUID id = servico.criar(new CriarEventoComando(
                req.promotorId(),
                req.localId(),
                req.titulo(),
                req.descricaoCurta(),
                req.descricaoLonga(),
                req.periodoInicio(),
                req.periodoFim(),
                req.categoria(),
                req.precoInteira(),
                req.precoMeia(),
                req.artistas(),
                req.datasApresentacao()
        ));
        return responderCriado(id.toString());
    }

    @Operation(summary = "Edita evento em RASCUNHO")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editar(
            @PathVariable UUID id,
            @RequestBody EditarEventoRequisicao req) {
        servico.editar(id, new EditarEventoComando(
                req.localId(),
                req.titulo(),
                req.descricaoCurta(),
                req.descricaoLonga(),
                req.periodoInicio(),
                req.periodoFim(),
                req.categoria(),
                req.precoInteira(),
                req.precoMeia(),
                req.artistas(),
                req.datasApresentacao()
        ));
        return responderSemConteudo();
    }

    @Operation(summary = "Submete evento para análise")
    @PostMapping("/{id}/submeter")
    public ResponseEntity<Map<String, String>> submeter(@PathVariable UUID id) {
        servico.submeterParaAnalise(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Aprova evento")
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<Map<String, String>> aprovar(@PathVariable UUID id) {
        servico.aprovar(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Reprova evento com feedback")
    @PostMapping("/{id}/reprovar")
    public ResponseEntity<Map<String, String>> reprovar(@PathVariable UUID id,
                                                         @RequestBody ReprovarEventoRequisicao req) {
        servico.reprovar(id, new FeedbackReprovacao(req.feedback()));
        return responderSemConteudo();
    }

    public record CriarEventoRequisicao(
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

    public record EditarEventoRequisicao(
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
