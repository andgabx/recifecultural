package recifecultural.apresentacao.bff.inteligencia;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.inteligencia.*;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Tag(name = "BFF — Inteligência")
@RestController
@RequestMapping("/api/bff/inteligencia")
public class InteligenciaBffControlador extends AbstractBffControlador {

    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();
    private static final ConcurrentMap<String, JsonNode> CACHE_JSON = new ConcurrentHashMap<>();

    private final InteligenciaServicoAplicacao inteligenciaServico;

    public InteligenciaBffControlador(InteligenciaServicoAplicacao inteligenciaServico) {
        this.inteligenciaServico = inteligenciaServico;
    }

    @Operation(summary = "Projeta receita estimada a partir de orçamento de marketing e patrocínio")
    @PostMapping("/prever-receita")
    public ResponseEntity<PrevisaoReceitaResposta> preverReceita(@Valid @RequestBody PrevisaoReceitaRequisicao req) {
        return responder(inteligenciaServico.preverReceita(req.getOrcamentoMarketing(), req.getPatrocinio()));
    }

    @Operation(summary = "Calcula risco de ausência (no-show) geral de um evento")
    @PostMapping("/prever-noshow")
    public ResponseEntity<PrevisaoNoShowResposta> preverNoShow(@Valid @RequestBody PrevisaoNoShowRequisicao req) {
        return responder(inteligenciaServico.preverNoShow(req.getEventoId()));
    }

    @Operation(summary = "Gera análise estratégica de um evento (receita, ocupação, público-alvo, risco)")
    @GetMapping("/analisar-evento/{eventoId}")
    public ResponseEntity<AnaliseEventoResposta> analisarEvento(@PathVariable UUID eventoId) {
        return responder(inteligenciaServico.analisarEvento(eventoId));
    }

    @Operation(summary = "Retorna série mensal de visitação por teatro (dados do banco; fallback no CSV 2023 se vazio)")
    @GetMapping("/visitacao")
    public ResponseEntity<JsonNode> visitacao() {
        return ResponseEntity.ok(carregarJson("inteligencia/visitacao.json"));
    }

    @Operation(summary = "Retorna métricas de no-show agregadas por tipo de ingresso, faixa de preço e categoria")
    @GetMapping("/noshow-por-grupo")
    public ResponseEntity<JsonNode> noShowPorGrupo() {
        return ResponseEntity.ok(carregarJson("inteligencia/noshow_grupos.json"));
    }

    @Operation(summary = "Retorna métricas de avaliação do classificador de no-show (acurácia, ROC, PR, importâncias)")
    @GetMapping("/metricas-classificador")
    public ResponseEntity<JsonNode> metricasClassificador() {
        return ResponseEntity.ok(carregarJson("inteligencia/metricas_classificador.json"));
    }

    @Operation(summary = "Retorna pontos para scatter de preço efetivo × receita real, segmentados por categoria")
    @GetMapping("/receita-scatter")
    public ResponseEntity<JsonNode> receitaScatter() {
        return ResponseEntity.ok(carregarJson("inteligencia/receita_scatter.json"));
    }

    private JsonNode carregarJson(String caminhoClasspath) {
        return CACHE_JSON.computeIfAbsent(caminhoClasspath, chave -> {
            try {
                ClassPathResource resource = new ClassPathResource(chave);
                return OBJECT_MAPPER.readTree(resource.getInputStream());
            } catch (IOException e) {
                throw new RuntimeException("Falha ao carregar recurso de inteligência: " + chave, e);
            }
        });
    }
}
