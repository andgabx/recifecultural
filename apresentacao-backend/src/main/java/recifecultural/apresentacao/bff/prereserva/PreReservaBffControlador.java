package recifecultural.apresentacao.bff.prereserva;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.agenda.prereserva.DuracaoPreReserva;
import recifecultural.dominio.agenda.prereserva.PreReservaId;
import recifecultural.dominio.agenda.prereserva.PreReservaServico;

import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Pré-reserva")
@RestController
@RequestMapping("/api/bff/pre-reservas")
public class PreReservaBffControlador extends AbstractBffControlador {

    private final PreReservaServico servico;

    public PreReservaBffControlador(PreReservaServico servico) {
        this.servico = servico;
    }

    @Operation(summary = "Reserva assento por 10 min — bloqueia até o pagamento confirmar")
    @PostMapping
    public ResponseEntity<Map<String, String>> reservar(@RequestBody ReservarRequisicao req) {
        PreReservaId id = servico.reservar(
                req.setorId(),
                req.assentoId(),
                req.usuarioId(),
                req.eventoId(),
                DuracaoPreReserva.PADRAO);
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Cancela pré-reserva e libera o assento")
    @DeleteMapping("/{preReservaId}")
    public ResponseEntity<Map<String, String>> cancelar(@PathVariable UUID preReservaId) {
        servico.cancelar(new PreReservaId(preReservaId));
        return responderSemConteudo();
    }

    public record ReservarRequisicao(UUID setorId, UUID assentoId, UUID usuarioId, UUID eventoId) {}
}
