package recifecultural.apresentacao.bff.catraca;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.catraca.CatracaServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "BFF — Catraca")
@RestController
@RequestMapping("/api/bff/catraca")
public class CatracaBffControlador extends AbstractBffControlador {

    private final CatracaServicoAplicacao servico;

    public CatracaBffControlador(CatracaServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Valida acesso com QR code e portão")
    @PostMapping("/validar")
    public ResponseEntity<Map<String, Object>> validarAcesso(@RequestBody ValidarAcessoRequisicao req) {
        try {
            String mensagem = servico.validarAcesso(req.codigoQr(), LocalDateTime.now(), req.portaoAcesso());
            return responder(Map.of("liberado", true, "motivo", mensagem));
        } catch (IllegalArgumentException e) {
            return responder(Map.of("liberado", false, "motivo", e.getMessage()));
        }
    }

    public record ValidarAcessoRequisicao(String codigoQr, String portaoAcesso) {}
}
