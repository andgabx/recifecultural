package recifecultural.apresentacao.bff.checkout;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.ingressos.IngressoServicoAplicacao;
import recifecultural.aplicacao.ingressos.IngressoServicoAplicacao.ItemCompraMultipla;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.prereserva.PreReservaId;
import recifecultural.dominio.agenda.prereserva.PreReservaServico;
import recifecultural.dominio.ingressos.IConfirmacaoReserva;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.TipoIngresso;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Checkout")
@RestController
@RequestMapping("/api/bff/checkout")
public class CheckoutControlador extends AbstractBffControlador {

    private final IngressoServicoAplicacao servico;
    private final PreReservaServico preReservaServico;

    public CheckoutControlador(IngressoServicoAplicacao servico,
                                PreReservaServico preReservaServico) {
        this.servico = servico;
        this.preReservaServico = preReservaServico;
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

        List<ItemCompraMultipla> itens = req.itens().stream()
                .map(i -> new ItemCompraMultipla(
                        i.preReservaId(), i.assentoId(),
                        TipoIngresso.valueOf(i.tipo()), i.valor()))
                .toList();

        List<IngressoId> ids = servico.comprarMultiplosComCupom(
                req.eventoId(), req.dataHoraApresentacao(),
                itens,
                MetodoPagamento.valueOf(req.metodoPagamento()),
                req.capacidadeMaxima(),
                req.codigoCupom(), req.cpfComprador(), req.categoriaEvento(),
                adaptador());

        List<String> idsStr = ids.stream().map(id -> id.valor().toString()).toList();
        return responder(Map.of("ids", idsStr, "total", idsStr.size()));
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
