package recifecultural.aplicacao.agenda.bloqueioadministrativo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoCanceladoPorBloqueioEvento;
import recifecultural.dominio.compartilhado.evento.EventoObservador;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class ObserverBloqueioTest {

    private EventoBarramento barramento;
    private INotificacaoServico notificacaoServico;

    @BeforeEach
    void setUp() {
        barramento = mock(EventoBarramento.class);
        notificacaoServico = mock(INotificacaoServico.class);
    }

    @Test
    void bloqueio_observador_registra_se_no_barramento() {
        new BloqueioNotificacaoObservador(barramento, notificacaoServico);

        verify(barramento, times(1)).adicionar(any(BloqueioNotificacaoObservador.class));
    }

    @Test
    void bloqueio_observador_reage_a_evento_e_envia_notificacao_ao_promotor() {
        BloqueioNotificacaoObservador observador =
                new BloqueioNotificacaoObservador(barramento, notificacaoServico);

        UUID eventoId = UUID.randomUUID();
        UUID promotorId = UUID.randomUUID();
        EventoCanceladoPorBloqueioEvento evento = new EventoCanceladoPorBloqueioEvento(
                eventoId,
                promotorId,
                "Show de Verão",
                "Manutenção emergencial no espaço",
                List.of());

        observador.observarEvento(evento);

        verify(notificacaoServico, times(1)).enviarNotificacao(
                eq(promotorId),
                contains("Show de Verão"),
                eq("EVENTO_CANCELADO"),
                eq(eventoId));
    }

    @Test
    void bloqueio_observador_envia_notificacao_para_cada_artista() {
        BloqueioNotificacaoObservador observador =
                new BloqueioNotificacaoObservador(barramento, notificacaoServico);

        UUID eventoId = UUID.randomUUID();
        UUID promotorId = UUID.randomUUID();
        UUID artista1 = UUID.randomUUID();
        UUID artista2 = UUID.randomUUID();
        UUID artista3 = UUID.randomUUID();

        EventoCanceladoPorBloqueioEvento evento = new EventoCanceladoPorBloqueioEvento(
                eventoId,
                promotorId,
                "Festival Recife",
                "Obras no local",
                List.of(artista1, artista2, artista3));

        observador.observarEvento(evento);

        verify(notificacaoServico, times(1)).enviarNotificacao(
                eq(artista1), any(String.class), eq("APRESENTACAO_CANCELADA_ARTISTA"), eq(eventoId));
        verify(notificacaoServico, times(1)).enviarNotificacao(
                eq(artista2), any(String.class), eq("APRESENTACAO_CANCELADA_ARTISTA"), eq(eventoId));
        verify(notificacaoServico, times(1)).enviarNotificacao(
                eq(artista3), any(String.class), eq("APRESENTACAO_CANCELADA_ARTISTA"), eq(eventoId));
    }

    @Test
    void ingresso_observador_registra_se_no_barramento() {
        new IngressoNotificacaoObservador(barramento, notificacaoServico);

        verify(barramento, times(1)).adicionar(any(IngressoNotificacaoObservador.class));
    }

    @Test
    void ingresso_observador_reage_a_evento_reembolso() {
        IngressoNotificacaoObservador observador =
                new IngressoNotificacaoObservador(barramento, notificacaoServico);

        UUID eventoId = UUID.randomUUID();
        UUID promotorId = UUID.randomUUID();
        EventoCanceladoPorBloqueioEvento evento = new EventoCanceladoPorBloqueioEvento(
                eventoId,
                promotorId,
                "Peça Teatral",
                "Cancelamento por bloqueio administrativo",
                List.of());

        observador.observarEvento(evento);

        verify(notificacaoServico, times(1)).enviarBroadcast(
                contains("Peça Teatral"),
                eq("TITULARES_INGRESSOS_EVENTO"),
                eq(eventoId));
        verifyNoMoreInteractions(notificacaoServico);
    }
}
