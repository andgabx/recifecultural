package recifecultural.apresentacao.bff.cupom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.ingressos.CupomServicoAplicacao;
import recifecultural.aplicacao.ingressos.CupomServicoAplicacao.CriarCupomComando;
import recifecultural.aplicacao.ingressos.CupomServicoAplicacao.CupomResumo;
import recifecultural.dominio.cupom.AplicarCupomServico;
import recifecultural.dominio.cupom.TipoDesconto;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Tag(name = "BFF — Cupom")
@RestController
@RequestMapping("/api/bff/cupons")
public class CupomBffControlador extends AbstractBffControlador {

    private final AplicarCupomServico aplicarServico;
    private final CupomServicoAplicacao gestaoServico;

    public CupomBffControlador(AplicarCupomServico aplicarServico, CupomServicoAplicacao gestaoServico) {
        this.aplicarServico = aplicarServico;
        this.gestaoServico = gestaoServico;
    }

    @Operation(summary = "Valida cupom e calcula desconto SEM consumir (use antes de finalizar)")
    @PostMapping("/preview")
    public ResponseEntity<Map<String, Object>> preview(@RequestBody AplicarCupomRequisicao req) {
        var preview = aplicarServico.previewDesconto(req.codigo(), req.cpf(), req.valor(), req.categoria());
        return responder(Map.of(
                "tipoDesconto",          preview.tipoDesconto(),
                "configuracaoDesconto",  preview.configuracaoDesconto(),
                "descontoCalculado",     preview.descontoCalculado(),
                "valorFinal",            preview.valorFinal()
        ));
    }

    @Operation(summary = "Aplica cupom e retorna valor final")
    @PostMapping("/aplicar")
    public ResponseEntity<Map<String, Object>> aplicar(@RequestBody AplicarCupomRequisicao req) {
        BigDecimal valorFinal = aplicarServico.aplicarDesconto(req.codigo(), req.cpf(), req.valor(), req.categoria());
        return responder(Map.of("valorFinal", valorFinal));
    }

    @Operation(summary = "Lista todos os cupons (gestão)")
    @GetMapping
    public ResponseEntity<List<CupomResumo>> listar() {
        return responder(gestaoServico.listar());
    }

    @Operation(summary = "Cria novo cupom (gestão)")
    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@RequestBody CriarCupomRequisicao req) {
        CupomResumo criado = gestaoServico.criar(new CriarCupomComando(
                req.codigo(),
                TipoDesconto.valueOf(req.tipoDesconto()),
                req.valorDesconto(),
                req.valorMinimoPedido() == null ? BigDecimal.ZERO : req.valorMinimoPedido(),
                req.limiteGlobal(),
                req.limitePorCpf(),
                req.dataInicio(),
                req.dataFim(),
                req.categoriaPermitida()
        ));
        return responderCriado(criado.id());
    }

    @Operation(summary = "Remove cupom (gestão)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deletar(@PathVariable String id) {
        gestaoServico.deletar(id);
        return responderSemConteudo();
    }

    public record AplicarCupomRequisicao(String codigo, String cpf, BigDecimal valor, String categoria) {}

    public record CriarCupomRequisicao(
            String codigo,
            String tipoDesconto,
            BigDecimal valorDesconto,
            BigDecimal valorMinimoPedido,
            int limiteGlobal,
            int limitePorCpf,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String categoriaPermitida
    ) {}
}
