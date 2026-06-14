package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.UUID;

/** Operação de cancelamento do sorteio: marca tudo como cancelado e avisa o público. */
public class CancelarOperacao extends OperacaoSorteioTemplate {

    public CancelarOperacao(ISorteioRepositorio sorteioRepositorio,
                            INotificacaoServico notificacaoServico,
                            UUID sorteioId) {
        super(sorteioRepositorio, notificacaoServico, sorteioId);
    }

    @Override
    protected void aplicarRegra(Sorteio sorteio) {
        sorteio.cancelar();
    }

    @Override
    protected void notificar(Sorteio sorteio) {
        notificacaoServico.enviarBroadcast(
                "O sorteio " + sorteioId + " foi cancelado.",
                "SORTEIO_CANCELADO", sorteioId);
    }
}
