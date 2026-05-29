package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.financeiro.FinanceiroServicoAplicacao;
import recifecultural.aplicacao.financeiro.IndicadoresResumo;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.financeiro.CategoriaDespesa;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.dominio.financeiro.ResultadoRegistroDespesa;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Financeiro")
@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroControlador extends AbstractBffControlador {

    private final FinanceiroServicoAplicacao servico;

    public FinanceiroControlador(FinanceiroServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Busca indicadores do período")
    @GetMapping("/indicadores")
    public ResponseEntity<IndicadoresResumo> indicadores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "0") int capacidadeTotal) {
        return responder(servico.buscarIndicadores(inicio, fim, capacidadeTotal));
    }

    @Operation(summary = "Registra despesa em orçamento")
    @PostMapping("/despesas")
    public ResponseEntity<Map<String, Object>> registrarDespesa(@RequestBody RegistrarDespesaRequisicao req) {
        ResultadoRegistroDespesa resultado = servico.registrarDespesa(
                new OrcamentoId(req.orcamentoId()), req.descricao(),
                req.valor(), CategoriaDespesa.valueOf(req.categoria()));
        return responder(Map.of(
                "despesaId", resultado.getDespesa().getId().valor().toString(),
                "alertaOrcamento", resultado.isAlertaOrcamento()));
    }

    record RegistrarDespesaRequisicao(UUID orcamentoId, String descricao, BigDecimal valor, String categoria) {}
}
