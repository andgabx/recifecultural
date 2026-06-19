package recifecultural.apresentacao.bff.acessibilidade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import recifecultural.aplicacao.agenda.acessibilidade.RecursoAcessibilidadeServicoAplicacao;
import recifecultural.aplicacao.agenda.acessibilidade.RecursoAcessibilidadeServicoAplicacao.RecursoResumo;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.acessibilidade.TipoRecursoAcessibilidade;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Acessibilidade")
@RestController
@RequestMapping("/api/bff/acessibilidade")
public class AcessibilidadeBffControlador extends AbstractBffControlador {

    private final RecursoAcessibilidadeServicoAplicacao servico;

    public AcessibilidadeBffControlador(RecursoAcessibilidadeServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista todos os recursos cadastrados (gestão)")
    @GetMapping
    public ResponseEntity<List<RecursoResumo>> listarTodos() {
        return responder(servico.listarTodos());
    }

    @Operation(summary = "Lista recursos ativos (CONFIRMADO) de um evento — visão pública")
    @GetMapping("/evento/{eventoId}/ativos")
    public ResponseEntity<List<RecursoResumo>> listarAtivosPorEvento(@PathVariable UUID eventoId) {
        return responder(servico.listarAtivosPorEvento(eventoId));
    }

    @Operation(summary = "Lista todos os recursos de um evento (gestão)")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<RecursoResumo>> listarPorEvento(@PathVariable UUID eventoId) {
        return responder(servico.listarPorEvento(eventoId));
    }

    @Operation(summary = "Marca recurso em uma apresentação")
    @PostMapping
    public ResponseEntity<Map<String, String>> marcar(@Valid @RequestBody MarcarRecursoRequisicao req) {
        RecursoResumo r = servico.marcar(
                UUID.fromString(req.apresentacaoId()),
                UUID.fromString(req.eventoId()),
                TipoRecursoAcessibilidade.valueOf(req.tipo()));
        return responderCriado(r.id());
    }

    @Operation(summary = "Remove recurso anunciado, registrando justificativa e notificando público")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remover(@PathVariable UUID id,
                                                       @Valid @RequestBody RemoverRecursoRequisicao req) {
        servico.remover(id, req.justificativa());
        return responderSemConteudo();
    }

    public record MarcarRecursoRequisicao(String apresentacaoId, String eventoId, String tipo) {}
    public record RemoverRecursoRequisicao(String justificativa) {}
}
