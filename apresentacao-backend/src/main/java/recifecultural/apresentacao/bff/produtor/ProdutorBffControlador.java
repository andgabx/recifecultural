package recifecultural.apresentacao.bff.produtor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import recifecultural.aplicacao.artista.produtor.ProdutorResumo;
import recifecultural.aplicacao.artista.produtor.ProdutorServicoAplicacao;
import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Produtores")
@RestController
@RequestMapping("/api/bff/produtores")
public class ProdutorBffControlador extends AbstractBffControlador {

    private final ProdutorServicoAplicacao servico;

    public ProdutorBffControlador(ProdutorServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista produtores")
    @GetMapping
    public ResponseEntity<List<ProdutorResumo>> listar() {
        return responder(servico.pesquisarResumos());
    }

    @Operation(summary = "Cadastra produtor")
    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@Valid @RequestBody ProdutorTelas.CadastrarProdutorRequisicao req) {
        ProdutorId id = servico.cadastrar(req.nomeFantasia(), new Cnpj(req.cnpj()), req.email(), req.telefone());
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Suspende produtor")
    @PostMapping("/{id}/suspender")
    public ResponseEntity<Map<String, String>> suspender(
            @PathVariable UUID id,
            @Valid @RequestBody ProdutorTelas.AcaoAdministrativaRequisicao req) {
        servico.suspender(new ProdutorId(id), req.responsavel(), req.motivo());
        return responderSemConteudo();
    }

    @Operation(summary = "Reativa produtor")
    @PostMapping("/{id}/reativar")
    public ResponseEntity<Map<String, String>> reativar(
            @PathVariable UUID id,
            @Valid @RequestBody ProdutorTelas.AcaoAdministrativaRequisicao req) {
        servico.reativar(new ProdutorId(id), req.responsavel(), req.motivo());
        return responderSemConteudo();
    }

    @Operation(summary = "Inativa produtor")
    @PostMapping("/{id}/inativar")
    public ResponseEntity<Map<String, String>> inativar(
            @PathVariable UUID id,
            @Valid @RequestBody ProdutorTelas.AcaoAdministrativaRequisicao req) {
        servico.inativar(new ProdutorId(id), req.responsavel(), req.motivo());
        return responderSemConteudo();
    }
}
