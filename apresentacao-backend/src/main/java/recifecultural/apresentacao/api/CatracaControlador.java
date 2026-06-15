package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.catraca.CatracaServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "API — Catraca")
@RestController
@RequestMapping("/api/catraca")
public class CatracaControlador extends AbstractBffControlador {

    private final CatracaServicoAplicacao servico;

    public CatracaControlador(CatracaServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Valida acesso pelo código QR do ingresso")
    @PostMapping("/validar")
    public ResponseEntity<Map<String, String>> validar(@RequestBody ValidarAcessoRequisicao req) {
        String resultado = servico.validarAcesso(req.codigoQr(), req.horario(), req.portaoAcesso());
        return responder(Map.of("resultado", resultado));
    }

    record ValidarAcessoRequisicao(String codigoQr, LocalDateTime horario, String portaoAcesso) {}
}
