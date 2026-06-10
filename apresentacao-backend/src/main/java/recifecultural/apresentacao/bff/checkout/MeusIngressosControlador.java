package recifecultural.apresentacao.bff.checkout;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.ingressos.IngressoResumo;
import recifecultural.aplicacao.ingressos.IngressoServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.ResultadoReembolso;
import recifecultural.dominio.ingressos.EstrategiaProcessamentoReembolso;
import recifecultural.dominio.ingressos.SeletorEstrategiaReembolso;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Meus Ingressos")
@RestController
@RequestMapping("/api/bff/meus-ingressos")
public class MeusIngressosControlador extends AbstractBffControlador {

    private final IngressoServicoAplicacao servico;
    private final SeletorEstrategiaReembolso seletorEstrategia = new SeletorEstrategiaReembolso();

    public MeusIngressosControlador(IngressoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista todos os ingressos (agrupa por evento no front)")
    @GetMapping
    public ResponseEntity<List<IngressoResumo>> listarTodos() {
        return responder(servico.listarTodos());
    }

    @Operation(summary = "Lista ingressos do evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<IngressoResumo>> listarPorEvento(@PathVariable UUID eventoId) {
        return responder(servico.pesquisarPorEvento(eventoId));
    }

    @Operation(summary = "Solicita reembolso (Observer dispara invalidação na catraca)")
    @PostMapping("/{ingressoId}/reembolso")
    public ResponseEntity<SimulacaoReembolsoResposta> solicitarReembolso(
            @PathVariable UUID ingressoId) {
        ResultadoReembolso resultado = servico.solicitarReembolso(
                new IngressoId(ingressoId), LocalDateTime.now());
        return responder(new SimulacaoReembolsoResposta(
                resultado.getValorReembolsado(),
                resultado.isProcessado(),
                resultado.getPrazoProcessamento()));
    }

    @Operation(summary = "Consulta prazo/descrição por método de pagamento (Strategy)")
    @GetMapping("/reembolso/estrategia")
    public ResponseEntity<Map<String, String>> consultarEstrategiaReembolso(
            @RequestParam String metodoPagamento) {
        MetodoPagamento metodo = MetodoPagamento.valueOf(metodoPagamento);
        EstrategiaProcessamentoReembolso estrategia = seletorEstrategia.selecionar(metodo);
        return responder(Map.of(
                "prazo", estrategia.prazoProcessamento(),
                "descricao", estrategia.descricao()));
    }
}
