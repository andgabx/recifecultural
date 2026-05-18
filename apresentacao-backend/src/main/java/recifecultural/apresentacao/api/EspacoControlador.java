package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.EspacoServico;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Espaços")
@RestController
@RequestMapping("/api/espacos")
public class EspacoControlador extends AbstractBffControlador {

    private final EspacoServico servico;

    public EspacoControlador(EspacoServico servico) {
        this.servico = servico;
    }

    @Operation(summary = "Cadastra espaço")
    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@RequestBody CadastrarEspacoRequisicao req) {
        EspacoId id = servico.cadastrarEspaco(req.nome(), req.capacidadeMaxima(), req.riderTecnico());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Atualiza capacidade máxima do espaço")
    @PutMapping("/{id}/capacidade")
    public ResponseEntity<Map<String, String>> atualizarCapacidade(
            @PathVariable UUID id,
            @RequestBody AtualizarCapacidadeRequisicao req) {
        servico.atualizarCapacidade(new EspacoId(id), req.novaCapacidade(), req.ingressosVendidosFuturos());
        return responderSemConteudo();
    }

    @Operation(summary = "Interdita espaço")
    @PostMapping("/{id}/interditar")
    public ResponseEntity<Map<String, String>> interditar(@PathVariable UUID id) {
        servico.interditarEspaco(new EspacoId(id));
        return responderSemConteudo();
    }

    record CadastrarEspacoRequisicao(String nome, int capacidadeMaxima, List<String> riderTecnico) {}
    record AtualizarCapacidadeRequisicao(int novaCapacidade, int ingressosVendidosFuturos) {}
}
