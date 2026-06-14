package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.agenda.sorteio.SorteioResumo;
import recifecultural.aplicacao.agenda.sorteio.SorteioServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.sorteio.Inscricao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Sorteios")
@RestController
@RequestMapping("/api/sorteios")
public class SorteioControlador extends AbstractBffControlador {

    private final SorteioServicoAplicacao servico;

    public SorteioControlador(SorteioServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista sorteios de um evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<SorteioResumo>> listarPorEvento(@PathVariable UUID eventoId) {
        return responder(servico.pesquisarPorEvento(eventoId));
    }

    @Operation(summary = "Cria sorteio")
    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@RequestBody CriarSorteioRequisicao req) {
        servico.criar(req.apresentacaoId(), req.eventoId(), req.vagas(),
                req.prazoInscricao(), req.dataApresentacao());
        return ResponseEntity.status(201).body(Map.of("status", "criado"));
    }

    @Operation(summary = "Inscreve espectador no sorteio")
    @PostMapping("/{id}/inscrever/{espectadorId}")
    public ResponseEntity<Map<String, String>> inscrever(
            @PathVariable UUID id, @PathVariable UUID espectadorId) {
        servico.inscrever(id, espectadorId);
        return responderSemConteudo();
    }

    @Operation(summary = "Realiza apuração do sorteio")
    @PostMapping("/{id}/apurar")
    public ResponseEntity<Map<String, String>> apurar(@PathVariable UUID id) {
        servico.apurar(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Registra desistência do espectador")
    @PostMapping("/{id}/desistir/{espectadorId}")
    public ResponseEntity<Map<String, String>> desistir(
            @PathVariable UUID id, @PathVariable UUID espectadorId) {
        servico.desistir(id, espectadorId);
        return responderSemConteudo();
    }

    @Operation(summary = "Cancela sorteio")
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Map<String, String>> cancelar(@PathVariable UUID id) {
        servico.cancelar(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Lista inscrições por prioridade")
    @GetMapping("/{id}/inscricoes")
    public ResponseEntity<List<Inscricao>> inscricoesPorPrioridade(@PathVariable UUID id) {
        return responder(servico.listarInscricoesPorPrioridade(id));
    }

    record CriarSorteioRequisicao(
            UUID apresentacaoId, UUID eventoId, int vagas,
            LocalDateTime prazoInscricao, LocalDateTime dataApresentacao) {}
}
