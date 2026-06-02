package recifecultural.apresentacao.bff.espaco;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioAdministrativoServicoAplicacao;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.EspacoServico;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Espaços")
@RestController
@RequestMapping("/api/bff/espacos")
public class EspacoBffControlador extends AbstractBffControlador {

    private final EspacoServico servico;
    private final BloqueioAdministrativoServicoAplicacao bloqueioServico;

    public EspacoBffControlador(EspacoServico servico,
                                 BloqueioAdministrativoServicoAplicacao bloqueioServico) {
        this.servico = servico;
        this.bloqueioServico = bloqueioServico;
    }

    @Operation(summary = "Lista todos os espaços (resumo)")
    @GetMapping
    public ResponseEntity<List<EspacoResumo>> listar() {
        List<EspacoResumo> resumos = servico.listarTodos().stream()
                .map(e -> new EspacoResumo(
                        e.getId().valor(),
                        e.getNome(),
                        e.getCapacidadeMaxima(),
                        e.getStatus() != null ? e.getStatus().name() : null))
                .toList();
        return responder(resumos);
    }

    @Operation(summary = "Cadastra espaço")
    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@RequestBody EspacoTelas.CadastrarEspacoRequisicao req) {
        EspacoId id = servico.cadastrarEspaco(req.nome(), req.capacidadeMaxima(), req.riderTecnico());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Atualiza capacidade do espaço")
    @PutMapping("/{id}/capacidade")
    public ResponseEntity<Map<String, String>> atualizarCapacidade(
            @PathVariable UUID id,
            @RequestBody EspacoTelas.AtualizarCapacidadeRequisicao req) {
        servico.atualizarCapacidade(new EspacoId(id), req.novaCapacidade(), req.ingressosVendidosFuturos());
        return responderSemConteudo();
    }

    @Operation(summary = "Interdita espaço")
    @PostMapping("/{id}/interditar")
    public ResponseEntity<Map<String, String>> interditar(@PathVariable UUID id) {
        servico.interditarEspaco(new EspacoId(id));
        return responderSemConteudo();
    }

    @Operation(summary = "Reativa espaço interditado e encerra bloqueios ativos")
    @PostMapping("/{id}/reativar")
    public ResponseEntity<Map<String, String>> reativar(@PathVariable UUID id) {
        EspacoId espacoId = new EspacoId(id);
        servico.reativarEspaco(espacoId);
        bloqueioServico.desativarBloqueiosAtivosDoEspaco(espacoId);
        return responderSemConteudo();
    }

    public record EspacoResumo(UUID id, String nome, int capacidadeMaxima, String status) {}
}
