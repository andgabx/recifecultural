package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.artista.produtor.ProdutorResumo;
import recifecultural.aplicacao.artista.produtor.ProdutorServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Produtores")
@RestController
@RequestMapping("/api/produtores")
public class ProdutorControlador extends AbstractBffControlador {

    private final ProdutorServicoAplicacao servico;

    public ProdutorControlador(ProdutorServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista produtores")
    @GetMapping
    public ResponseEntity<List<ProdutorResumo>> listar() {
        return responder(servico.pesquisarResumos());
    }

    @Operation(summary = "Cadastra produtor")
    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@RequestBody CadastrarProdutorRequisicao req) {
        ProdutorId id = servico.cadastrar(req.nomeFantasia(), new Cnpj(req.cnpj()), req.email(), req.telefone());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Suspende produtor")
    @PostMapping("/{id}/suspender")
    public ResponseEntity<Map<String, String>> suspender(
            @PathVariable UUID id, @RequestBody AcaoAdministrativaRequisicao req) {
        servico.suspender(new ProdutorId(id), req.responsavel(), req.motivo());
        return responderSemConteudo();
    }

    @Operation(summary = "Reativa produtor")
    @PostMapping("/{id}/reativar")
    public ResponseEntity<Map<String, String>> reativar(
            @PathVariable UUID id, @RequestBody AcaoAdministrativaRequisicao req) {
        servico.reativar(new ProdutorId(id), req.responsavel(), req.motivo());
        return responderSemConteudo();
    }

    @Operation(summary = "Inativa produtor")
    @PostMapping("/{id}/inativar")
    public ResponseEntity<Map<String, String>> inativar(
            @PathVariable UUID id, @RequestBody AcaoAdministrativaRequisicao req) {
        servico.inativar(new ProdutorId(id), req.responsavel(), req.motivo());
        return responderSemConteudo();
    }

    record CadastrarProdutorRequisicao(String nomeFantasia, String cnpj, String email, String telefone) {}
    record AcaoAdministrativaRequisicao(String responsavel, String motivo) {}
}
