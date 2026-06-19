package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.EspacoServico;
import recifecultural.dominio.ingressos.IIngressoRepositorio;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Espaços")
@RestController
@RequestMapping("/api/espacos")
public class EspacoControlador extends AbstractBffControlador {

    private final EspacoServico servico;
    private final IIngressoRepositorio ingressoRepositorio;

    public EspacoControlador(EspacoServico servico, IIngressoRepositorio ingressoRepositorio) {
        this.servico = servico;
        this.ingressoRepositorio = ingressoRepositorio;
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
        // ingressosVendidosFuturos é computado server-side; nunca confiamos no valor do cliente.
        int ingressosVendidosFuturos = ingressoRepositorio.maiorCargaAtivosPorEspaco(id, LocalDateTime.now());
        servico.atualizarCapacidade(new EspacoId(id), req.novaCapacidade(), ingressosVendidosFuturos);
        return responderSemConteudo();
    }

    @Operation(summary = "Interdita espaço")
    @PostMapping("/{id}/interditar")
    public ResponseEntity<Map<String, String>> interditar(@PathVariable UUID id) {
        servico.interditarEspaco(new EspacoId(id));
        return responderSemConteudo();
    }

    @Operation(summary = "Reativa espaço interditado")
    @PostMapping("/{id}/reativar")
    public ResponseEntity<Map<String, String>> reativar(@PathVariable UUID id) {
        servico.reativarEspaco(new EspacoId(id));
        return responderSemConteudo();
    }

    record CadastrarEspacoRequisicao(String nome, int capacidadeMaxima, List<String> riderTecnico) {}
    record AtualizarCapacidadeRequisicao(int novaCapacidade) {}
}
