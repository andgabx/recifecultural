package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.UUID;

/** Operação de inscrição de um espectador no sorteio (sem notificação). */
public class InscreverOperacao extends OperacaoSorteioTemplate {

    private final UUID espectadorId;

    public InscreverOperacao(ISorteioRepositorio sorteioRepositorio,
                             INotificacaoServico notificacaoServico,
                             UUID sorteioId,
                             UUID espectadorId) {
        super(sorteioRepositorio, notificacaoServico, sorteioId);
        this.espectadorId = espectadorId;
    }

    @Override
    protected void aplicarRegra(Sorteio sorteio) {
        sorteio.inscrever(espectadorId);
    }
}
