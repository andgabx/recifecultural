package recifecultural.apresentacao.bff.setor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.ingressos.IngressoServicoAplicacao;
import recifecultural.dominio.agenda.prereserva.PreReserva;
import recifecultural.dominio.agenda.prereserva.PreReservaServico;
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
import java.util.Set;
import java.util.UUID;

@Tag(name = "BFF — Setores")
@RestController
@RequestMapping("/api/bff/setores")
public class SetorBffControlador extends AbstractBffControlador {

    private final GestaoAmbienteInternoServico servico;
    private final IngressoServicoAplicacao ingressoServico;
    private final PreReservaServico preReservaServico;

    public SetorBffControlador(GestaoAmbienteInternoServico servico,
                               IngressoServicoAplicacao ingressoServico,
                               PreReservaServico preReservaServico) {
        this.servico = servico;
        this.ingressoServico = ingressoServico;
        this.preReservaServico = preReservaServico;
    }

    @Operation(summary = "Lista setores do espaço com seus assentos (mapa de cadeiras)")
    @GetMapping("/espaco/{espacoId}")
    public ResponseEntity<List<SetorComAssentos>> listarPorEspaco(
            @PathVariable UUID espacoId,
            @RequestParam(required = false) UUID eventoId) {
        List<Setor> setores = servico.listarPorEspaco(new EspacoId(espacoId));

        Set<UUID> ocupados = eventoId != null
                ? ingressoServico.buscarAssentosOcupadosPorEvento(eventoId)
                : null;

        Set<UUID> preReservados = eventoId != null
                ? preReservaServico.listarAtivasPorEvento(eventoId).stream()
                        .map(PreReserva::getAssentoId)
                        .collect(java.util.stream.Collectors.toSet())
                : null;

        List<SetorComAssentos> resumos = setores.stream().map(s -> new SetorComAssentos(
                s.getId().valor(),
                s.getEspacoId().valor(),
                s.getNome(),
                s.getTipoSetor().name(),
                s.getFileirasHorizontais(),
                s.getAssentosPorFileiraVertical(),
                s.getAssentos().stream()
                        .map(a -> assentoResumo(a, ocupados, preReservados))
                        .toList()
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

    private static AssentoResumo assentoResumo(Assento a, Set<UUID> ocupados, Set<UUID> preReservados) {
        if (ocupados == null) {
            return assentoResumo(a);
        }
        StatusAssento status;
        if (a.getStatus() == StatusAssento.BLOQUEADO) {
            status = StatusAssento.BLOQUEADO;
        } else if (preReservados != null && preReservados.contains(a.getId())) {
            status = StatusAssento.PRE_RESERVADO;
        } else if (ocupados.contains(a.getId())) {
            status = StatusAssento.OCUPADO;
        } else {
            status = StatusAssento.LIVRE;
        }
        return new AssentoResumo(
                a.getId(),
                a.getCodigo(),
                a.getFileira(),
                a.getNumero(),
                status,
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
