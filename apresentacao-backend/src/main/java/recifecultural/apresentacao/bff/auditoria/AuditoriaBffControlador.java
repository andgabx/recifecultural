package recifecultural.apresentacao.bff.auditoria;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.auditoria.AuditoriaServicoAplicacao;
import recifecultural.aplicacao.auditoria.AuditoriaServicoAplicacao.RegistroResumo;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.List;

@Tag(name = "BFF — Auditoria")
@RestController
@RequestMapping("/api/bff/auditoria")
public class AuditoriaBffControlador extends AbstractBffControlador {

    private final AuditoriaServicoAplicacao servico;

    public AuditoriaBffControlador(AuditoriaServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista os registros de auditoria mais recentes (Decorator F4.1)")
    @GetMapping
    public ResponseEntity<List<RegistroResumo>> listar(
            @RequestParam(required = false) String entidade,
            @RequestParam(defaultValue = "100") int limite) {
        if (entidade != null && !entidade.isBlank()) {
            return responder(servico.listarPorEntidade(entidade.toUpperCase(), limite));
        }
        return responder(servico.listarRecentes(limite));
    }
}
