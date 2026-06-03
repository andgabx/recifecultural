package recifecultural.apresentacao.bff.sorteio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.agenda.sorteio.SorteioInscritoResumo;
import recifecultural.aplicacao.agenda.sorteio.SorteioResumo;
import recifecultural.aplicacao.agenda.sorteio.SorteioServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.sorteio.Inscricao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Sorteio")
@RestController
@RequestMapping("/api/bff/sorteios")
public class SorteioBffControlador extends AbstractBffControlador {

    private final SorteioServicoAplicacao servico;

    public SorteioBffControlador(SorteioServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista sorteios com inscrições abertas")
    @GetMapping("/abertos")
    public ResponseEntity<List<SorteioResumo>> listarAbertos() {
        return responder(servico.pesquisarAbertos());
    }

    @Operation(summary = "Lista sorteios do evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<SorteioResumo>> listarPorEvento(@PathVariable UUID eventoId) {
        return responder(servico.pesquisarPorEvento(eventoId));
    }

    @Operation(summary = "Lista sorteios em que o espectador está inscrito (com status da inscrição e posição)")
    @GetMapping("/espectador/{espectadorId}")
    public ResponseEntity<List<SorteioInscritoResumo>> listarPorEspectador(@PathVariable UUID espectadorId) {
        return responder(servico.pesquisarPorEspectador(espectadorId));
    }

    @Operation(summary = "Cria sorteio")
    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@RequestBody CriarSorteioRequisicao req) {
        servico.criar(req.apresentacaoId(), req.eventoId(), req.vagas(),
                req.prazoInscricao(), req.dataApresentacao());
        return ResponseEntity.status(201).body(Map.of("status", "criado"));
    }

    @Operation(summary = "Inscreve espectador no sorteio")
    @PostMapping("/{sorteioId}/inscrever/{espectadorId}")
    public ResponseEntity<Map<String, String>> inscrever(
            @PathVariable UUID sorteioId,
            @PathVariable UUID espectadorId) {
        servico.inscrever(sorteioId, espectadorId);
        return responderSemConteudo();
    }

    @Operation(summary = "Apura sorteio")
    @PostMapping("/{sorteioId}/apurar")
    public ResponseEntity<Map<String, String>> apurar(@PathVariable UUID sorteioId) {
        servico.apurar(sorteioId);
        return responderSemConteudo();
    }

    @Operation(summary = "Registra desistência do espectador")
    @PostMapping("/{sorteioId}/desistir/{espectadorId}")
    public ResponseEntity<Map<String, String>> desistir(
            @PathVariable UUID sorteioId,
            @PathVariable UUID espectadorId) {
        servico.desistir(sorteioId, espectadorId);
        return responderSemConteudo();
    }

    @Operation(summary = "Cancela sorteio")
    @PostMapping("/{sorteioId}/cancelar")
    public ResponseEntity<Map<String, String>> cancelar(@PathVariable UUID sorteioId) {
        servico.cancelar(sorteioId);
        return responderSemConteudo();
    }

    @Operation(summary = "Lista inscrições por prioridade (Iterator)")
    @GetMapping("/{sorteioId}/inscricoes")
    public ResponseEntity<List<Inscricao>> listarInscricoesPorPrioridade(@PathVariable UUID sorteioId) {
        return responder(servico.listarInscricoesPorPrioridade(sorteioId));
    }
}
