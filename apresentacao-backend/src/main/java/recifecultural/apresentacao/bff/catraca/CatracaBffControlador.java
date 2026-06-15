package recifecultural.apresentacao.bff.catraca;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
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

    @Operation(summary = "Valida acesso com QR code, horário e portão (Observer dispara invalidação)")
    @PostMapping("/validar")
    public ResponseEntity<Map<String, String>> validarAcesso(@RequestBody ValidarAcessoRequisicao req) {
        String resultado = servico.validarAcesso(req.codigoQr(), req.horario(), req.portaoAcesso());
        return responder(Map.of("resultado", resultado));
    }

    public record ValidarAcessoRequisicao(
            String codigoQr,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime horario,
            String portaoAcesso) {}
}
