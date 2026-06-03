package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioAdministrativoResumo;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioAdministrativoServicoAplicacao;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.EventoConflitanteResumo;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Bloqueios")
@RestController
@RequestMapping("/api/bloqueios")
public class BloqueioControlador extends AbstractBffControlador {

    private final BloqueioAdministrativoServicoAplicacao servico;

    public BloqueioControlador(BloqueioAdministrativoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista bloqueios ativos")
    @GetMapping
    public ResponseEntity<List<BloqueioAdministrativoResumo>> listarAtivos() {
        return responder(servico.pesquisarAtivos());
    }

    @Operation(summary = "Pré-visualiza eventos que seriam cancelados pelo bloqueio")
    @GetMapping("/preview")
    public ResponseEntity<List<EventoConflitanteResumo>> preview(
            @RequestParam UUID espacoId,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        return responder(servico.previewConflitos(new EspacoId(espacoId), inicio, fim));
    }

    @Operation(summary = "Cria bloqueio administrativo")
    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@RequestBody CriarBloqueioRequisicao req) {
        servico.criar(new EspacoId(req.espacoId()), req.inicio(), req.fim(), req.justificativa());
        return ResponseEntity.status(201).body(Map.of("status", "criado"));
    }

    @Operation(summary = "Desativa bloqueio")
    @PostMapping("/{id}/desativar")
    public ResponseEntity<Map<String, String>> desativar(@PathVariable UUID id) {
        servico.desativar(BloqueioAdministrativoId.de(id.toString()), false);
        return responderSemConteudo();
    }

    record CriarBloqueioRequisicao(UUID espacoId, LocalDate inicio, LocalDate fim, String justificativa) {}
}
