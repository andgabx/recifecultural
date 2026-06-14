package recifecultural.dominio.agenda.acessibilidade;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.Objects;

public abstract class OperacaoRecursoAcessibilidadeTemplate {

    private final IRecursoAcessibilidadeRepositorio repositorio;
    private final INotificacaoServico notificacaoServico;

    protected OperacaoRecursoAcessibilidadeTemplate(IRecursoAcessibilidadeRepositorio repositorio,
                                                    INotificacaoServico notificacaoServico) {
        this.repositorio = Objects.requireNonNull(repositorio, "Repositório é obrigatório.");
        this.notificacaoServico = Objects.requireNonNull(notificacaoServico, "Serviço de notificação é obrigatório.");
    }

    public final void executar() {
        RecursoAcessibilidade recurso = buscarRecurso();
        RecursoAcessibilidade.RecursoEvento evento = aplicarRegraDeDominio(recurso);
        persistir(recurso);
        notificar(evento);
    }

    protected abstract RecursoAcessibilidade buscarRecurso();

    protected abstract RecursoAcessibilidade.RecursoEvento aplicarRegraDeDominio(RecursoAcessibilidade recurso);

    protected void persistir(RecursoAcessibilidade recurso) {
        repositorio.atualizar(recurso);
    }

    protected abstract void notificar(RecursoAcessibilidade.RecursoEvento evento);

    protected final IRecursoAcessibilidadeRepositorio repositorio() {
        return repositorio;
    }

    protected final INotificacaoServico notificacaoServico() {
        return notificacaoServico;
    }
}
