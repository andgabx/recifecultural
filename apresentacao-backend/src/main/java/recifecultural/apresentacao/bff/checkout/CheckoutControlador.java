package recifecultural.apresentacao.bff.checkout;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.ingressos.IngressoServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.prereserva.PreReservaId;
import recifecultural.dominio.agenda.prereserva.PreReservaServico;
import recifecultural.dominio.cupom.AplicarCupomServico;
import recifecultural.dominio.ingressos.IConfirmacaoReserva;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.TipoIngresso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Checkout")
@RestController
@RequestMapping("/api/bff/checkout")
public class CheckoutControlador extends AbstractBffControlador {

    private final IngressoServicoAplicacao servico;
    private final PreReservaServico preReservaServico;
    private final AplicarCupomServico cupomServico;

    public CheckoutControlador(IngressoServicoAplicacao servico,
                                PreReservaServico preReservaServico,
                                AplicarCupomServico cupomServico) {
        this.servico = servico;
        this.preReservaServico = preReservaServico;
        this.cupomServico = cupomServico;
    }

    @Operation(summary = "Compra ingresso simples")
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

    @Operation(summary = "Compra um ingresso confirmando pré-reserva de assento")
    @PostMapping("/comprar-com-prereserva")
    public ResponseEntity<Map<String, String>> comprarComPreReserva(
            @RequestBody CompraComPreReservaRequisicao req) {
        IngressoId id = servico.comprarComPreReserva(
                req.eventoId(), req.dataHoraApresentacao(),
                req.preReservaId(), req.assentoId(),
                TipoIngresso.valueOf(req.tipo()),
                req.valor(), MetodoPagamento.valueOf(req.metodoPagamento()),
                req.capacidadeMaxima(),
                adaptador());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Compra múltiplos ingressos com assentos pré-reservados")
    @PostMapping("/comprar-multiplos")
    public ResponseEntity<Map<String, Object>> comprarMultiplos(
            @RequestBody CompraMultiplaRequisicao req) {

        List<CompraMultiplaRequisicao.ItemCompra> itens = req.itens();
        boolean temCupom = req.codigoCupom() != null && !req.codigoCupom().isBlank()
                && req.cpfComprador() != null && !req.cpfComprador().isBlank()
                && req.categoriaEvento() != null && !req.categoriaEvento().isBlank();

        // Calcula o total bruto e o desconto sobre ele, depois distribui por item
        List<BigDecimal> valoresFinal = new ArrayList<>();
        if (temCupom) {
            BigDecimal totalBruto = itens.stream()
                    .map(CompraMultiplaRequisicao.ItemCompra::valor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // preview não consome o cupom; o consumo ocorre abaixo via aplicarDesconto
            var preview = cupomServico.previewDesconto(
                    req.codigoCupom(), req.cpfComprador(), totalBruto, req.categoriaEvento());
            BigDecimal totalComDesconto = preview.valorFinal();
            BigDecimal fator = totalBruto.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ONE
                    : totalComDesconto.divide(totalBruto, 10, RoundingMode.HALF_UP);

            for (CompraMultiplaRequisicao.ItemCompra item : itens) {
                valoresFinal.add(item.valor().multiply(fator).setScale(2, RoundingMode.HALF_UP));
            }
            // Registra o uso do cupom uma única vez
            cupomServico.aplicarDesconto(req.codigoCupom(), req.cpfComprador(), totalBruto, req.categoriaEvento());
        } else {
            itens.forEach(item -> valoresFinal.add(item.valor()));
        }

        List<String> ids = new ArrayList<>();
        for (int i = 0; i < itens.size(); i++) {
            CompraMultiplaRequisicao.ItemCompra item = itens.get(i);
            IngressoId id = servico.comprarComPreReserva(
                    req.eventoId(), req.dataHoraApresentacao(),
                    item.preReservaId(), item.assentoId(),
                    TipoIngresso.valueOf(item.tipo()),
                    valoresFinal.get(i),
                    MetodoPagamento.valueOf(req.metodoPagamento()),
                    req.capacidadeMaxima(),
                    adaptador());
            ids.add(id.valor().toString());
        }
        return responder(Map.of("ids", ids, "total", ids.size()));
    }

    private IConfirmacaoReserva adaptador() {
        return new IConfirmacaoReserva() {
            @Override
            public void confirmar(UUID preReservaId) {
                preReservaServico.confirmar(new PreReservaId(preReservaId));
            }
            @Override
            public void cancelar(UUID preReservaId) {
                preReservaServico.cancelar(new PreReservaId(preReservaId));
            }
        };
    }
}
