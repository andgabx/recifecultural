package recifecultural.apresentacao.api;

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

@Tag(name = "API — Ingressos")
@RestController
@RequestMapping("/api/ingressos")
public class IngressoControlador extends AbstractBffControlador {

    private final IngressoServicoAplicacao servico;

    public IngressoControlador(IngressoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista ingressos de um evento")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<IngressoResumo>> listarPorEvento(@PathVariable UUID eventoId) {
        return responder(servico.pesquisarPorEvento(eventoId));
    }

    @Operation(summary = "Compra ingresso")
    @PostMapping
    public ResponseEntity<Map<String, String>> comprar(@RequestBody CompraRequisicao req) {
        IngressoId id = servico.comprar(req.eventoId(), req.dataHoraApresentacao(),
                TipoIngresso.valueOf(req.tipo()), req.valor(),
                MetodoPagamento.valueOf(req.metodoPagamento()), req.capacidadeMaxima());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Compra ingresso com cupom de desconto")
    @PostMapping("/com-cupom")
    public ResponseEntity<Map<String, String>> comprarComCupom(@RequestBody CompraComCupomRequisicao req) {
        IngressoId id = servico.comprarComCupom(req.eventoId(), req.dataHoraApresentacao(),
                TipoIngresso.valueOf(req.tipo()), req.valor(),
                MetodoPagamento.valueOf(req.metodoPagamento()), req.capacidadeMaxima(),
                req.codigoCupom(), req.cpfComprador(), req.categoriaEvento());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Solicita reembolso do ingresso")
    @PostMapping("/{id}/reembolso")
    public ResponseEntity<Map<String, Object>> reembolsar(@PathVariable UUID id) {
        ResultadoReembolso resultado = servico.solicitarReembolso(new IngressoId(id), LocalDateTime.now());
        return responder(Map.of(
                "valorReembolsado", resultado.getValorReembolsado(),
                "processado", resultado.isProcessado(),
                "prazo", resultado.getPrazoProcessamento()));
    }

    record CompraRequisicao(
            UUID eventoId, LocalDateTime dataHoraApresentacao,
            String tipo, BigDecimal valor, String metodoPagamento, int capacidadeMaxima) {}

    record CompraComCupomRequisicao(
            UUID eventoId, LocalDateTime dataHoraApresentacao,
            String tipo, BigDecimal valor, String metodoPagamento, int capacidadeMaxima,
            String codigoCupom, String cpfComprador, String categoriaEvento) {}
}
