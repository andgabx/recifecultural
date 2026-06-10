package recifecultural.aplicacao.agenda.bloqueioadministrativo;

import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoCanceladoPorBloqueioEvento;
import recifecultural.dominio.compartilhado.evento.EventoObservador;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

public class IngressoNotificacaoObservador implements EventoObservador<EventoCanceladoPorBloqueioEvento> {

    private final INotificacaoServico notificacaoServico;

    public IngressoNotificacaoObservador(EventoBarramento barramento, INotificacaoServico notificacaoServico) {
        if (barramento == null) throw new IllegalArgumentException("EventoBarramento não pode ser nulo.");
        if (notificacaoServico == null) throw new IllegalArgumentException("INotificacaoServico não pode ser nulo.");
        this.notificacaoServico = notificacaoServico;
        barramento.adicionar(this);
    }

    @Override
    public void observarEvento(EventoCanceladoPorBloqueioEvento evento) {
        String mensagem = String.format(
                "O evento '%s' para o qual você possui ingresso foi cancelado. Justificativa: %s",
                evento.tituloEvento(), evento.justificativaBloqueio());

        notificacaoServico.enviarBroadcast(
                mensagem,
                "TITULARES_INGRESSOS_EVENTO",
                evento.eventoId());
    }
}
