package recifecultural.aplicacao.agenda.bloqueioadministrativo;

import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoCanceladoPorBloqueioEvento;
import recifecultural.dominio.compartilhado.evento.EventoObservador;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;


public class BloqueioNotificacaoObservador implements EventoObservador<EventoCanceladoPorBloqueioEvento> {

    private final INotificacaoServico notificacaoServico;

    public BloqueioNotificacaoObservador(EventoBarramento barramento, INotificacaoServico notificacaoServico) {
        if (barramento == null) throw new IllegalArgumentException("EventoBarramento não pode ser nulo.");
        if (notificacaoServico == null) throw new IllegalArgumentException("INotificacaoServico não pode ser nulo.");
        this.notificacaoServico = notificacaoServico;
        barramento.adicionar(this);
    }

    @Override
    public void observarEvento(EventoCanceladoPorBloqueioEvento evento) {
        String mensagem = String.format(
                "Atenção: O evento '%s' foi cancelado por motivos técnicos. Justificativa: %s",
                evento.tituloEvento(), evento.justificativaBloqueio());

        notificacaoServico.enviarNotificacao(
                evento.promotorId(),
                mensagem,
                "EVENTO_CANCELADO",
                evento.eventoId());

        if (evento.artistaIds() != null) {
            String mensagemArtista = String.format(
                    "Sua apresentação no evento '%s' foi cancelada por motivos técnicos. Justificativa: %s",
                    evento.tituloEvento(), evento.justificativaBloqueio());
            for (java.util.UUID artistaId : evento.artistaIds()) {
                notificacaoServico.enviarNotificacao(
                        artistaId,
                        mensagemArtista,
                        "APRESENTACAO_CANCELADA_ARTISTA",
                        evento.eventoId());
            }
        }

        notificacaoServico.enviarBroadcast(
                mensagem,
                "PARTICIPANTES_EVENTO_CANCELADO",
                evento.eventoId());
    }
}
