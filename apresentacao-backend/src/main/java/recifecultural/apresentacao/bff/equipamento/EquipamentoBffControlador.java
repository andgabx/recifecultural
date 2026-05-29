package recifecultural.apresentacao.bff.equipamento;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.agenda.equipamento.EquipamentoServicoAplicacao;
import recifecultural.aplicacao.agenda.equipamento.EquipamentoServicoAplicacao.EquipamentoResumo;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Equipamento")
@RestController
@RequestMapping("/api/bff/equipamentos")
public class EquipamentoBffControlador extends AbstractBffControlador {

    private final EquipamentoServicoAplicacao servico;

    public EquipamentoBffControlador(EquipamentoServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista equipamentos de um espaço")
    @GetMapping("/espaco/{espacoId}")
    public ResponseEntity<List<EquipamentoResumo>> listarPorEspaco(@PathVariable UUID espacoId) {
        return responder(servico.listarPorEspaco(espacoId));
    }

    @Operation(summary = "Adquire novo equipamento para um espaço")
    @PostMapping
    public ResponseEntity<Map<String, String>> adquirir(@RequestBody AdquirirRequisicao req) {
        String id = servico.adquirir(UUID.fromString(req.espacoId()), req.nome());
        return responderCriado(id);
    }

    @Operation(summary = "Envia equipamento para manutenção (notifica evento alocado se houver)")
    @PostMapping("/{id}/manutencao")
    public ResponseEntity<Map<String, String>> manutencao(@PathVariable UUID id) {
        servico.marcarManutencao(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Libera equipamento manualmente (devolve ao status DISPONIVEL)")
    @PostMapping("/{id}/liberar")
    public ResponseEntity<Map<String, String>> liberar(@PathVariable UUID id) {
        servico.liberar(id);
        return responderSemConteudo();
    }

    @Operation(summary = "Remove equipamento (somente se não estiver ALOCADO)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> remover(@PathVariable UUID id) {
        servico.remover(id);
        return responderSemConteudo();
    }

    public record AdquirirRequisicao(String espacoId, String nome) {}
}
