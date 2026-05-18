package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.agenda.evento.EventoResumo;
import recifecultural.aplicacao.agenda.evento.EventoResumoExpandido;
import recifecultural.aplicacao.agenda.evento.EventoServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.evento.FeedbackReprovacao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Eventos")
@RestController
@RequestMapping("/api/eventos")
public class EventoControlador extends AbstractBffControlador {

    private final EventoServicoAplicacao servico;

    public EventoControlador(EventoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista todos os eventos (resumos)")
    @GetMapping
    public ResponseEntity<List<EventoResumo>> listar() {
        return responder(servico.pesquisarResumos());
    }

    @Operation(summary = "Busca evento expandido por id")
    @GetMapping("/{id}")
    public ResponseEntity<EventoResumoExpandido> buscar(@PathVariable UUID id) {
        return responder(servico.buscarResumoExpandido(id));
    }

    @Operation(summary = "Submete evento para análise")
    @PostMapping("/{id}/submeter")
    public ResponseEntity<Map<String, String>> submeter(@PathVariable UUID id) {
        servico.submeterParaAnalise(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Aprova evento (gestor)")
    @PostMapping("/{id}/aprovar")
    public ResponseEntity<Map<String, String>> aprovar(@PathVariable UUID id) {
        servico.aprovar(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Reprova evento com feedback")
    @PostMapping("/{id}/reprovar")
    public ResponseEntity<Map<String, String>> reprovar(
            @PathVariable UUID id,
            @RequestBody ReprovarRequisicao req) {
        servico.reprovar(id, new FeedbackReprovacao(req.feedback()));
        return responderSemConteudo();
    }

    record ReprovarRequisicao(String feedback) {}
}
