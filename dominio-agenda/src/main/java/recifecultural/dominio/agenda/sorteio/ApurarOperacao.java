package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.UUID;

/** Operação de apuração do sorteio: sorteia ganhadores e notifica cada um. */
public class ApurarOperacao extends OperacaoSorteioTemplate {

    public ApurarOperacao(ISorteioRepositorio sorteioRepositorio,
                          INotificacaoServico notificacaoServico,
                          UUID sorteioId) {
        super(sorteioRepositorio, notificacaoServico, sorteioId);
    }

    @Override
    protected void aplicarRegra(Sorteio sorteio) {
        sorteio.apurar();
    }

    @Override
    protected void notificar(Sorteio sorteio) {
        sorteio.getInscricoes().stream()
                .filter(i -> i.getStatus() == StatusInscricao.GANHADOR)
                .forEach(i -> notificacaoServico.enviarNotificacao(
                        i.getEspectadorId(),
                        "Parabéns! Você foi sorteado para o evento " + sorteio.getEventoId(),
                        "SORTEIO_GANHADOR", sorteioId));
    }
}
