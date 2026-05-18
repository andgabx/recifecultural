package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.setor.GestaoAmbienteInternoServico;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.TipoSetor;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Setores")
@RestController
@RequestMapping("/api/setores")
public class SetorControlador extends AbstractBffControlador {

    private final GestaoAmbienteInternoServico servico;

    public SetorControlador(GestaoAmbienteInternoServico servico) {
        this.servico = servico;
    }

    @Operation(summary = "Configura setor interno (gera assentos)")
    @PostMapping
    public ResponseEntity<Map<String, String>> configurar(@RequestBody ConfigurarSetorRequisicao req) {
        Setor setor = servico.configurarGestaoAmbiente(
                new EspacoId(req.espacoId()), req.nome(),
                TipoSetor.valueOf(req.tipoSetor()),
                req.fileirasHorizontais(), req.assentosPorFileira());
        return responderCriado(setor.getId().valor().toString());
    }

    @Operation(summary = "Conta assentos disponíveis do espaço")
    @GetMapping("/espaco/{espacoId}/capacidade")
    public ResponseEntity<Map<String, Long>> capacidade(@PathVariable UUID espacoId) {
        long disponivel = servico.contarAssentosDisponiveisPorEspaco(new EspacoId(espacoId));
        return responder(Map.of("assentosDisponiveis", disponivel));
    }

    record ConfigurarSetorRequisicao(
            UUID espacoId, String nome, String tipoSetor,
            int fileirasHorizontais, int assentosPorFileira) {}
}
