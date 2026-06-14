package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.agenda.comum.OperacaoDominioTemplate;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.UUID;

/*
 * Template Method especializado para operações que alteram um Sorteio.
 * Fixa os passos "buscar" (pelo id) e "persistir" (atualizar); cada operação
 * concreta só precisa preencher a regra de negócio e, se quiser, a notificação.
 */
public abstract class OperacaoSorteioTemplate extends OperacaoDominioTemplate<Sorteio> {

    protected final ISorteioRepositorio sorteioRepositorio;
    protected final INotificacaoServico notificacaoServico;
    protected final UUID sorteioId;

    protected OperacaoSorteioTemplate(ISorteioRepositorio sorteioRepositorio,
                                      INotificacaoServico notificacaoServico,
                                      UUID sorteioId) {
        this.sorteioRepositorio = sorteioRepositorio;
        this.notificacaoServico = notificacaoServico;
        this.sorteioId = sorteioId;
    }

    @Override
    protected Sorteio buscar() {
        return sorteioRepositorio.obter(sorteioId)
                .orElseThrow(() -> new IllegalArgumentException("Sorteio não encontrado: " + sorteioId));
    }

    @Override
    protected void persistir(Sorteio sorteio) {
        sorteioRepositorio.atualizar(sorteio);
    }
}
