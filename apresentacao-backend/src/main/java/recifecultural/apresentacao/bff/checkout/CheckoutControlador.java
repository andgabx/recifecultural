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
import recifecultural.dominio.ingressos.TipoIngresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Checkout")
@RestController
@RequestMapping("/api/bff/checkout")
public class CheckoutControlador extends AbstractBffControlador {

    private final IngressoServicoAplicacao servico;

    public CheckoutControlador(IngressoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Compra ingresso (Decorator com log atua no gateway)")
    @PostMapping("/comprar")
    public ResponseEntity<Map<String, String>> comprar(@RequestBody CompraRequisicao req) {
        IngressoId id = servico.comprar(
                req.eventoId(), req.dataHoraApresentacao(),
                TipoIngresso.valueOf(req.tipo()),
                req.valor(), MetodoPagamento.valueOf(req.metodoPagamento()),
                req.capacidadeMaxima());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Compra ingresso com cupom de desconto")
    @PostMapping("/comprar-com-cupom")
    public ResponseEntity<Map<String, String>> comprarComCupom(@RequestBody CompraComCupomRequisicao req) {
        IngressoId id = servico.comprarComCupom(
                req.eventoId(), req.dataHoraApresentacao(),
                TipoIngresso.valueOf(req.tipo()),
                req.valor(), MetodoPagamento.valueOf(req.metodoPagamento()),
                req.capacidadeMaxima(),
                req.codigoCupom(), req.cpfComprador(), req.categoriaEvento());
        return responderCriado(id.valor().toString());
    }
}
