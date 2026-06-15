package recifecultural.aplicacao.agenda.acessibilidade;

import recifecultural.dominio.agenda.acessibilidade.IRecursoAcessibilidadeRepositorio;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidade;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidadeServico;
import recifecultural.dominio.agenda.acessibilidade.TipoRecursoAcessibilidade;

import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class RecursoAcessibilidadeServicoAplicacao {

    private final RecursoAcessibilidadeServico servico;
    private final IRecursoAcessibilidadeRepositorio repositorio;

    public RecursoAcessibilidadeServicoAplicacao(RecursoAcessibilidadeServico servico,
                                                  IRecursoAcessibilidadeRepositorio repositorio) {
        notNull(servico, "RecursoAcessibilidadeServico não pode ser nulo.");
        notNull(repositorio, "IRecursoAcessibilidadeRepositorio não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<RecursoResumo> listarTodos() {
        return repositorio.listarTodos().stream().map(this::toResumo).toList();
    }

    public List<RecursoResumo> listarAtivosPorEvento(UUID eventoId) {
        return repositorio.listarAtivosPorEvento(eventoId).stream().map(this::toResumo).toList();
    }

    public List<RecursoResumo> listarPorEvento(UUID eventoId) {
        return repositorio.listarPorEvento(eventoId).stream().map(this::toResumo).toList();
    }

    public RecursoResumo marcar(UUID apresentacaoId, UUID eventoId, TipoRecursoAcessibilidade tipo) {
        RecursoAcessibilidade recurso = servico.marcar(apresentacaoId, eventoId, tipo);
        return toResumo(recurso);
    }

    public void remover(UUID recursoId, String justificativa) {
        servico.remover(recursoId, justificativa);
    }

    private RecursoResumo toResumo(RecursoAcessibilidade r) {
        return new RecursoResumo(
                r.getId().toString(),
                r.getApresentacaoId() != null ? r.getApresentacaoId().toString() : null,
                r.getEventoId().toString(),
                r.getTipo().name(),
                r.getStatus().name(),
                r.getJustificativaRemocao()
        );
    }

    public record RecursoResumo(
            String id,
            String apresentacaoId,
            String eventoId,
            String tipo,
            String status,
            String justificativaRemocao
    ) {}
}
