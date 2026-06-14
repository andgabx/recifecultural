package recifecultural.dominio.agenda.acessibilidade;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.List;
import java.util.UUID;

public class RecursoAcessibilidadeServico {

    private final IRecursoAcessibilidadeRepositorio repositorio;
    private final IEventoRepositorio eventoRepositorio;
    private final INotificacaoServico notificacaoServico;

    public RecursoAcessibilidadeServico(IRecursoAcessibilidadeRepositorio repositorio,
                                        IEventoRepositorio eventoRepositorio,
                                        INotificacaoServico notificacaoServico) {
        this.repositorio = repositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.notificacaoServico = notificacaoServico;
    }

    public RecursoAcessibilidade marcar(UUID apresentacaoId, UUID eventoId, TipoRecursoAcessibilidade tipo) {
        Evento evento = eventoRepositorio.obter(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + eventoId));
        evento.verificarAprovado();

        boolean jaExiste = repositorio.listarPorApresentacao(apresentacaoId).stream()
                .anyMatch(r -> r.getTipo() == tipo && r.getStatus() == StatusRecurso.CONFIRMADO);
        if (jaExiste)
            throw new IllegalStateException("Recurso " + tipo + " já marcado para esta apresentação.");

        RecursoAcessibilidade recurso = new RecursoAcessibilidade(apresentacaoId, eventoId, tipo);
        repositorio.salvar(recurso);
        return recurso;
    }

    public void remover(UUID recursoId, String justificativa) {
        new RemocaoRecursoAcessibilidadeOperacao(repositorio, notificacaoServico, recursoId, justificativa)
                .executar();
    }

    public List<RecursoAcessibilidade> listarAtivosPorEvento(UUID eventoId) {
        return repositorio.listarAtivosPorEvento(eventoId);
    }
}
