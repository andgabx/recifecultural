package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.agenda.sorteio.Sorteio.SuplentePromovidoEvento;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.Optional;
import java.util.UUID;

/** Operação de desistência de um ganhador: promove um suplente e o notifica, se houver. */
public class DesistirOperacao extends OperacaoSorteioTemplate {

    private final UUID espectadorId;
    private Optional<SuplentePromovidoEvento> promocao = Optional.empty();

    public DesistirOperacao(ISorteioRepositorio sorteioRepositorio,
                            INotificacaoServico notificacaoServico,
                            UUID sorteioId,
                            UUID espectadorId) {
        super(sorteioRepositorio, notificacaoServico, sorteioId);
        this.espectadorId = espectadorId;
    }

    @Override
    protected void aplicarRegra(Sorteio sorteio) {
        this.promocao = sorteio.desistir(espectadorId);
    }

    @Override
    protected void notificar(Sorteio sorteio) {
        promocao.ifPresent(evento -> notificacaoServico.enviarNotificacao(
                evento.getEspectadorPromovidoId(),
                "Parabéns! Você foi promovido de suplente para ganhador no sorteio " + sorteioId,
                "SORTEIO_PROMOCAO", sorteioId));
    }
}
