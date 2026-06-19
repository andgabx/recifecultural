package recifecultural.apresentacao.bff.comentario;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import recifecultural.aplicacao.agenda.comentario.ComentarioServicoAplicacao;
import recifecultural.aplicacao.agenda.comentario.ComentarioServicoAplicacao.ComentarioResumo;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Comentários")
@RestController
@RequestMapping("/api/bff/comentarios")
public class ComentarioBffControlador extends AbstractBffControlador {

    private final ComentarioServicoAplicacao servico;

    public ComentarioBffControlador(ComentarioServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista comentários ativos de um evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<ComentarioResumo>> listar(@PathVariable UUID eventoId) {
        return responder(servico.listarPorEvento(eventoId));
    }

    @Operation(summary = "Posta um comentário (Decorator de moderação atua aqui)")
    @PostMapping
    public ResponseEntity<Map<String, String>> postar(@Valid @RequestBody PostarRequisicao req) {
        UUID id = servico.postar(
                req.eventoId(), req.espectadorId(),
                req.texto(), req.comentarioPaiId());
        return responderCriado(id.toString());
    }

    @Operation(summary = "Curte um comentário (espectador não pode curtir o próprio)")
    @PostMapping("/{id}/curtir")
    public ResponseEntity<Map<String, String>> curtir(
            @PathVariable UUID id,
            @Valid @RequestBody CurtirRequisicao req) {
        servico.curtir(id, req.espectadorId());
        return responderSemConteudo();
    }

    @Operation(summary = "Soft delete (status DELETADO; preserva hierarquia)")
    @PostMapping("/{id}/deletar")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable UUID id) {
        servico.deletar(id);
        return responderSemConteudo();
    }

    public record PostarRequisicao(
            UUID eventoId,
            UUID espectadorId,
            String texto,
            UUID comentarioPaiId
    ) {}

    public record CurtirRequisicao(UUID espectadorId) {}
}
