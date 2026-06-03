package recifecultural.dominio.agenda.evento;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EventoTest {

    private Evento novoRascunho() {
        return new Evento(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Evento X",
                "curta",
                "longa",
                new Periodo(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)),
                URI.create("https://evento.com/x"),
                new Preco(java.math.BigDecimal.TEN, java.math.BigDecimal.ONE, null));
    }

    private Evento eventoEmAnalisePronto() {
        Evento e = novoRascunho();
        e.adicionarArtista(UUID.randomUUID());
        e.definirCategoria("TEATRO");
        e.programarApresentacao(LocalDateTime.now().plusDays(7));
        e.submeterParaAnalise();
        return e;
    }

    @Test
    void nasce_em_rascunho() {
        Evento e = novoRascunho();
        assertEquals(StatusEvento.RASCUNHO, e.getStatus());
    }

    @Test
    void submeter_sem_apresentacao_falha() {
        Evento e = novoRascunho();
        e.adicionarArtista(UUID.randomUUID());
        e.definirCategoria("TEATRO");
        assertThrows(IllegalStateException.class, e::submeterParaAnalise);
    }

    @Test
    void submeter_sem_artista_falha() {
        Evento e = novoRascunho();
        e.definirCategoria("TEATRO");
        e.programarApresentacao(LocalDateTime.now().plusDays(7));
        assertThrows(IllegalStateException.class, e::submeterParaAnalise);
    }

    @Test
    void submeter_sem_categoria_falha() {
        Evento e = novoRascunho();
        e.adicionarArtista(UUID.randomUUID());
        e.programarApresentacao(LocalDateTime.now().plusDays(7));
        assertThrows(IllegalStateException.class, e::submeterParaAnalise);
    }

    @Test
    void aprovar_de_em_analise_funciona_e_retorna_evento() {
        Evento e = eventoEmAnalisePronto();
        Evento.AprovadoEvento evento = e.aprovar();
        assertEquals(StatusEvento.APROVADO, e.getStatus());
        assertNotNull(e.getDataAprovacao());
        assertSame(e, evento.getEvento());
    }

    @Test
    void aprovar_evento_que_nao_esta_em_analise_falha() {
        Evento e = novoRascunho();
        assertThrows(IllegalStateException.class, e::aprovar);
    }

    @Test
    void reprovar_retorna_evento() {
        Evento e = eventoEmAnalisePronto();
        Evento.ReprovadoEvento evento = e.reprovar(new FeedbackReprovacao("Feedback de motivo bem extenso e justificado."));
        assertEquals(StatusEvento.REPROVADO, e.getStatus());
        assertSame(e, evento.getEvento());
    }

    @Test
    void cancelar_evento_retorna_evento() {
        Evento e = eventoEmAnalisePronto();
        e.aprovar();
        Evento.CanceladoEvento evento = e.cancelar("Motivo qualquer");
        assertEquals(StatusEvento.CANCELADO, e.getStatus());
        assertSame(e, evento.getEvento());
    }

    @Test
    void cancelar_sem_motivo_falha() {
        Evento e = novoRascunho();
        assertThrows(IllegalArgumentException.class, () -> e.cancelar(""));
    }

    @Test
    void titulo_em_branco_falha() {
        assertThrows(IllegalArgumentException.class, () -> new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "", "x", "y",
                new Periodo(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)),
                URI.create("https://x.com"),
                new Preco(java.math.BigDecimal.ONE, java.math.BigDecimal.ONE, null)));
    }
}
