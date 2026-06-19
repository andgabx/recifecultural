package recifecultural.apresentacao.bff.bloqueio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
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

@Tag(name = "BFF — Bloqueios")
@RestController
@RequestMapping("/api/bff/bloqueios")
public class BloqueioBffControlador extends AbstractBffControlador {

    private final BloqueioAdministrativoServicoAplicacao servico;

    public BloqueioBffControlador(BloqueioAdministrativoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista todos os bloqueios (ativos e histórico)")
    @GetMapping
    public ResponseEntity<List<BloqueioAdministrativoResumo>> listar() {
        return responder(servico.pesquisarTodos());
    }

    @Operation(summary = "Pré-visualiza eventos conflitantes com o período informado")
    @GetMapping("/preview")
    public ResponseEntity<List<EventoConflitanteResumo>> preview(
            @RequestParam UUID espacoId,
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {
        return responder(servico.previewConflitos(new EspacoId(espacoId), inicio, fim));
    }

    @Operation(summary = "Cria bloqueio administrativo")
    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@Valid @RequestBody CriarBloqueioRequisicao req) {
        servico.criar(new EspacoId(req.espacoId()), req.inicio(), req.fim(), req.justificativa());
        return ResponseEntity.status(201).body(Map.of("status", "criado"));
    }

    @Operation(summary = "Desativa bloqueio, com opção de reativar eventos cancelados")
    @PostMapping("/{id}/desativar")
    public ResponseEntity<Map<String, String>> desativar(
            @PathVariable UUID id,
            @Valid @RequestBody DesativarBloqueioRequisicao req) {
        servico.desativar(BloqueioAdministrativoId.de(id.toString()), req.reativarEventos());
        return responderSemConteudo();
    }

    record DesativarBloqueioRequisicao(boolean reativarEventos) {}
}
