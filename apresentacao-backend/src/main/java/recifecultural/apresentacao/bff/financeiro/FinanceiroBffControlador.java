package recifecultural.apresentacao.bff.financeiro;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.financeiro.FinanceiroServicoAplicacao;
import recifecultural.aplicacao.financeiro.IndicadoresResumo;
import recifecultural.dominio.financeiro.CategoriaDespesa;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.dominio.financeiro.ResultadoRegistroDespesa;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Financeiro")
@RestController
@RequestMapping("/api/bff/financeiro")
public class FinanceiroBffControlador extends AbstractBffControlador {

    private final FinanceiroServicoAplicacao servico;

    public FinanceiroBffControlador(FinanceiroServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Indicadores do período (ocupação, receita bruta/líquida, despesas)")
    @GetMapping("/indicadores")
    public ResponseEntity<IndicadoresResumo> indicadores(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(defaultValue = "0") int capacidadeTotal) {
        return responder(servico.buscarIndicadores(inicio, fim, capacidadeTotal));
    }

    @Operation(summary = "Registra despesa + flag de alerta de orçamento")
    @PostMapping("/despesas")
    public ResponseEntity<Map<String, Object>> registrarDespesa(@RequestBody FinanceiroTelas.RegistrarDespesaRequisicao req) {
        ResultadoRegistroDespesa resultado = servico.registrarDespesa(
                new OrcamentoId(req.orcamentoId()),
                req.descricao(),
                req.valor(),
                CategoriaDespesa.valueOf(req.categoria()));
        return responder(Map.of(
                "despesaId", resultado.getDespesa().getId().valor().toString(),
                "alertaOrcamento", resultado.isAlertaOrcamento()));
    }
}
