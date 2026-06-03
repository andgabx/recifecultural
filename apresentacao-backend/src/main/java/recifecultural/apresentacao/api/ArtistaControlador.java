package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.artista.artista.ArtistaResumo;
import recifecultural.aplicacao.artista.artista.ArtistaServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Artistas")
@RestController
@RequestMapping("/api/artistas")
public class ArtistaControlador extends AbstractBffControlador {

    private final ArtistaServicoAplicacao servico;

    public ArtistaControlador(ArtistaServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista artistas")
    @GetMapping
    public ResponseEntity<List<ArtistaResumo>> listar() {
        return responder(servico.pesquisarResumos());
    }

    @Operation(summary = "Cadastra artista")
    @PostMapping
    public ResponseEntity<Map<String, String>> cadastrar(@RequestBody CadastrarArtistaRequisicao req) {
        ArtistaId id = servico.cadastrar(new ProdutorId(req.produtorId()), req.nome(),
                ArtistaServicoAplicacao.construirRider(req.riderItens()));
        return responderCriado(id.valor().toString());
    }

    @Operation(summary = "Inativa artista")
    @PostMapping("/{id}/inativar")
    public ResponseEntity<Map<String, String>> inativar(@PathVariable UUID id) {
        servico.inativar(ArtistaId.de(id.toString()));
        return responderSemConteudo();
    }

    record CadastrarArtistaRequisicao(UUID produtorId, String nome, List<String> riderItens) {}
}
