package recifecultural.apresentacao.bff.setor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.setor.Assento;
import recifecultural.dominio.espaco.setor.GestaoAmbienteInternoServico;
import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.StatusAssento;
import recifecultural.dominio.espaco.setor.TipoSetor;
import recifecultural.apresentacao.bff.AbstractBffControlador;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Setores")
@RestController
@RequestMapping("/api/bff/setores")
public class SetorBffControlador extends AbstractBffControlador {

    private final GestaoAmbienteInternoServico servico;

    public SetorBffControlador(GestaoAmbienteInternoServico servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista setores do espaço com seus assentos (mapa de cadeiras)")
    @GetMapping("/espaco/{espacoId}")
    public ResponseEntity<List<SetorComAssentos>> listarPorEspaco(@PathVariable UUID espacoId) {
        List<Setor> setores = servico.listarPorEspaco(new EspacoId(espacoId));
        List<SetorComAssentos> resumos = setores.stream().map(s -> new SetorComAssentos(
                s.getId().valor(),
                s.getEspacoId().valor(),
                s.getNome(),
                s.getTipoSetor().name(),
                s.getFileirasHorizontais(),
                s.getAssentosPorFileiraVertical(),
                s.getAssentos().stream().map(SetorBffControlador::assentoResumo).toList()
        )).toList();
        return responder(resumos);
    }

    @Operation(summary = "Configura setor + gera assentos")
    @PostMapping
    public ResponseEntity<Map<String, String>> configurar(@RequestBody SetorTelas.ConfigurarSetorRequisicao req) {
        Setor setor = servico.configurarGestaoAmbiente(
                new EspacoId(req.espacoId()),
                req.nome(),
                TipoSetor.valueOf(req.tipoSetor()),
                req.fileirasHorizontais(),
                req.assentosPorFileiraVertical());
        return responderCriado(setor.getId().valor().toString());
    }

    @Operation(summary = "Edita setor (nome/tipo livremente; dimensões só se sem reservas)")
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> editar(
            @PathVariable UUID id,
            @RequestBody SetorTelas.EditarSetorRequisicao req) {
        servico.editarSetor(
                new recifecultural.dominio.espaco.setor.SetorId(id),
                req.nome(),
                TipoSetor.valueOf(req.tipoSetor()),
                req.fileirasHorizontais(),
                req.assentosPorFileiraVertical());
        return responderSemConteudo();
    }

    @Operation(summary = "Contagem de assentos disponíveis por espaço")
    @GetMapping("/espaco/{espacoId}/capacidade")
    public ResponseEntity<Map<String, Long>> capacidadeDisponivel(@PathVariable UUID espacoId) {
        long disponivel = servico.contarAssentosDisponiveisPorEspaco(new EspacoId(espacoId));
        return responder(Map.of("assentosDisponiveis", disponivel));
    }

    private static AssentoResumo assentoResumo(Assento a) {
        return new AssentoResumo(
                a.getId(),
                a.getCodigo(),
                a.getFileira(),
                a.getNumero(),
                a.getStatus(),
                a.getMotivoIndisponibilidade()
        );
    }

    public record SetorComAssentos(
            UUID id,
            UUID espacoId,
            String nome,
            String tipoSetor,
            int fileirasHorizontais,
            int assentosPorFileiraVertical,
            List<AssentoResumo> assentos
    ) {}

    public record AssentoResumo(
            UUID id,
            String codigo,
            String fileira,
            int numero,
            StatusAssento status,
            MotivoIndisponibilidadeAssento motivoIndisponibilidade
    ) {}
}
