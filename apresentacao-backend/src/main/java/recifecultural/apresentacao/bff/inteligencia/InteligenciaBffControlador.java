package recifecultural.apresentacao.bff.inteligencia;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.inteligencia.*;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.UUID;

@Tag(name = "BFF — Inteligência")
@RestController
@RequestMapping("/api/bff/inteligencia")
public class InteligenciaBffControlador extends AbstractBffControlador {

    private final InteligenciaServicoAplicacao inteligenciaServico;

    public InteligenciaBffControlador(InteligenciaServicoAplicacao inteligenciaServico) {
        this.inteligenciaServico = inteligenciaServico;
    }

    @Operation(summary = "Projeta receita estimada a partir de orçamento de marketing e patrocínio")
    @PostMapping("/prever-receita")
    public ResponseEntity<PrevisaoReceitaResposta> preverReceita(@RequestBody PrevisaoReceitaRequisicao req) {
        return responder(inteligenciaServico.preverReceita(req.getOrcamentoMarketing(), req.getPatrocinio()));
    }

    @Operation(summary = "Calcula risco de ausência (no-show) geral de um evento")
    @PostMapping("/prever-noshow")
    public ResponseEntity<PrevisaoNoShowResposta> preverNoShow(@RequestBody PrevisaoNoShowRequisicao req) {
        return responder(inteligenciaServico.preverNoShow(req.getEventoId()));
    }

    @Operation(summary = "Gera análise estratégica de um evento (receita, ocupação, público-alvo, risco)")
    @GetMapping("/analisar-evento/{eventoId}")
    public ResponseEntity<AnaliseEventoResposta> analisarEvento(@PathVariable UUID eventoId) {
        return responder(inteligenciaServico.analisarEvento(eventoId));
    }
}