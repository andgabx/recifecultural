package recifecultural.dominio.agenda.acessibilidade;

import recifecultural.dominio.agenda.comum.OperacaoDominioTemplate;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.UUID;

/*
 * Template Method especializado para operações que alteram um RecursoAcessibilidade.
 * Fixa os passos "buscar" (pelo id) e "persistir" (atualizar); cada operação
 * concreta só preenche a regra de negócio e, se quiser, a notificação.
 */
public abstract class OperacaoRecursoAcessibilidadeTemplate extends OperacaoDominioTemplate<RecursoAcessibilidade> {

    protected final IRecursoAcessibilidadeRepositorio repositorio;
    protected final INotificacaoServico notificacaoServico;
    protected final UUID recursoId;

    protected OperacaoRecursoAcessibilidadeTemplate(IRecursoAcessibilidadeRepositorio repositorio,
                                                    INotificacaoServico notificacaoServico,
                                                    UUID recursoId) {
        this.repositorio = repositorio;
        this.notificacaoServico = notificacaoServico;
        this.recursoId = recursoId;
    }

    @Override
    protected RecursoAcessibilidade buscar() {
        return repositorio.obter(recursoId)
                .orElseThrow(() -> new IllegalArgumentException("Recurso não encontrado: " + recursoId));
    }

    @Override
    protected void persistir(RecursoAcessibilidade recurso) {
        repositorio.atualizar(recurso);
    }
}
