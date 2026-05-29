package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.cupom.AplicarCupomServico;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "API — Cupons")
@RestController
@RequestMapping("/api/cupons")
public class CupomControlador extends AbstractBffControlador {

    private final AplicarCupomServico servico;

    public CupomControlador(AplicarCupomServico servico) {
        this.servico = servico;
    }

    @Operation(summary = "Aplica cupom e retorna valor com desconto")
    @PostMapping("/aplicar")
    public ResponseEntity<Map<String, Object>> aplicar(@RequestBody AplicarCupomRequisicao req) {
        BigDecimal valorFinal = servico.aplicarDesconto(req.codigo(), req.cpf(), req.valor(), req.categoria());
        return responder(Map.of("valorFinal", valorFinal));
    }

    record AplicarCupomRequisicao(String codigo, String cpf, BigDecimal valor, String categoria) {}
}
