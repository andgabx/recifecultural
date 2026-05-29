package recifecultural.aplicacao.agenda.bloqueioadministrativo;

import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoCanceladoPorBloqueioEvento;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoServico;

/*
 * Padrão Observer (Par 3): assinante do EventoBarramento. Quando um bloqueio
 * administrativo (F3.1) cancela um evento, este observador reage publicando
 * notificações (F3.2) para o promotor e para os participantes, sem acoplar
 * o serviço de bloqueios ao contexto de notificações.
 */
public class BloqueioNotificacaoObservador {

    private final NotificacaoServico notificacaoServico;

    public BloqueioNotificacaoObservador(EventoBarramento barramento, NotificacaoServico notificacaoServico) {
        if (barramento == null) throw new IllegalArgumentException("EventoBarramento não pode ser nulo.");
        if (notificacaoServico == null) throw new IllegalArgumentException("NotificacaoServico não pode ser nulo.");
        this.notificacaoServico = notificacaoServico;
        barramento.adicionar(this::reagirCancelamentoPorBloqueio);
    }

    void reagirCancelamentoPorBloqueio(EventoCanceladoPorBloqueioEvento evento) {
        String mensagem = String.format(
                "Atenção: O evento '%s' foi cancelado por motivos técnicos. Justificativa: %s",
                evento.tituloEvento(), evento.justificativaBloqueio());

        notificacaoServico.enviarNotificacao(
                evento.promotorId(),
                mensagem,
                "EVENTO_CANCELADO",
                null);

        notificacaoServico.enviarBroadcast(
                mensagem,
                "PARTICIPANTES_EVENTO_CANCELADO",
                null);
    }
}
