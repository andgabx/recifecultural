package recifecultural.dominio.agenda.acessibilidade;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.evento.StatusEvento;
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
        if (evento.getStatus() != StatusEvento.APROVADO)
            throw new IllegalStateException("Recursos de acessibilidade só podem ser marcados em eventos aprovados.");

        boolean jaExiste = repositorio.listarPorApresentacao(apresentacaoId).stream()
                .anyMatch(r -> r.getTipo() == tipo && r.getStatus() == StatusRecurso.CONFIRMADO);
        if (jaExiste)
            throw new IllegalStateException("Recurso " + tipo + " já marcado para esta apresentação.");

        RecursoAcessibilidade recurso = new RecursoAcessibilidade(apresentacaoId, eventoId, tipo);
        repositorio.salvar(recurso);
        return recurso;
    }

    public void remover(UUID recursoId, String justificativa) {
        RecursoAcessibilidade recurso = repositorio.obter(recursoId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado: " + recursoId));
        recurso.remover(justificativa);
        repositorio.atualizar(recurso);

        String mensagem = String.format(
                "O recurso de acessibilidade %s foi removido do evento %s. Justificativa: %s",
                recurso.getTipo(), recurso.getEventoId(), justificativa);
        notificacaoServico.enviarBroadcast(mensagem, "ACESSIBILIDADE_REMOVIDA", recurso.getEventoId());
    }

    public List<RecursoAcessibilidade> listarAtivosPorEvento(UUID eventoId) {
        return repositorio.listarAtivosPorEvento(eventoId);
    }
}
