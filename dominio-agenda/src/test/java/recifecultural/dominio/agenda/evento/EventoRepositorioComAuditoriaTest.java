package recifecultural.dominio.agenda.evento;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import recifecultural.dominio.compartilhado.auditoria.AcaoAuditoria;
import recifecultural.dominio.compartilhado.auditoria.IAuditoriaRepositorio;
import recifecultural.dominio.compartilhado.auditoria.RegistroAuditoria;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventoRepositorioComAuditoriaTest {

    private IEventoRepositorio delegado;
    private IAuditoriaRepositorio auditoriaRepositorio;
    private EventoRepositorioComAuditoria decorator;

    @BeforeEach
    void setUp() {
        delegado = mock(IEventoRepositorio.class);
        auditoriaRepositorio = mock(IAuditoriaRepositorio.class);
        decorator = new EventoRepositorioComAuditoria(delegado, auditoriaRepositorio);
    }

    private Evento novoEvento() {
        return new Evento(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Show de Jazz no Pátio",
                "Descrição curta do evento",
                "Descrição longa do evento de jazz",
                new Periodo(LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10)),
                URI.create("https://recifecultural.com/jazz"),
                new Preco(java.math.BigDecimal.TEN, java.math.BigDecimal.ONE, null));
    }

    private Evento eventoComStatus(StatusEvento status) {
        return new Evento(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Peça de Teatro no Recife",
                "Descrição curta",
                "Descrição longa",
                new Periodo(LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(8)),
                new Preco(java.math.BigDecimal.TEN, java.math.BigDecimal.ONE, null),
                "TEATRO",
                status,
                java.util.List.of(LocalDateTime.now().plusDays(4)),
                java.util.List.of(UUID.randomUUID()),
                null,
                null,
                false,
                null);
    }

    @Test
    void salvar_registra_auditoria_com_acao_criado() {
        Evento evento = novoEvento();

        decorator.salvar(evento);

        ArgumentCaptor<RegistroAuditoria> captor = ArgumentCaptor.forClass(RegistroAuditoria.class);
        verify(auditoriaRepositorio).registrar(captor.capture());
        RegistroAuditoria registro = captor.getValue();
        assertEquals(AcaoAuditoria.CRIADO, registro.getAcao());
        assertEquals(evento.getId(), registro.getEntidadeId());
        assertEquals("EVENTO", registro.getEntidade());
        assertEquals(evento.getStatus().name(), registro.getStatusNovo());
        assertNull(registro.getStatusAnterior());
    }

    @Test
    void atualizar_registra_auditoria_com_acao_transicao() {
        Evento eventoAnterior = eventoComStatus(StatusEvento.RASCUNHO);
        Evento eventoAtualizado = eventoComStatus(StatusEvento.EM_ANALISE);

        when(delegado.obter(eventoAtualizado.getId())).thenReturn(Optional.of(eventoAnterior));

        decorator.atualizar(eventoAtualizado);

        ArgumentCaptor<RegistroAuditoria> captor = ArgumentCaptor.forClass(RegistroAuditoria.class);
        verify(auditoriaRepositorio).registrar(captor.capture());
        RegistroAuditoria registro = captor.getValue();
        assertEquals(AcaoAuditoria.TRANSICAO_STATUS, registro.getAcao());
        assertEquals(eventoAtualizado.getId(), registro.getEntidadeId());
        assertEquals(StatusEvento.RASCUNHO.name(), registro.getStatusAnterior());
        assertEquals(StatusEvento.EM_ANALISE.name(), registro.getStatusNovo());
    }

    @Test
    void salvar_tambem_chama_repositorio_delegado() {
        Evento evento = novoEvento();

        decorator.salvar(evento);

        verify(delegado).salvar(evento);
    }

    @Test
    void atualizar_tambem_chama_repositorio_delegado() {
        Evento eventoAnterior = eventoComStatus(StatusEvento.EM_ANALISE);
        Evento eventoAtualizado = eventoComStatus(StatusEvento.APROVADO);

        when(delegado.obter(eventoAtualizado.getId())).thenReturn(Optional.of(eventoAnterior));

        decorator.atualizar(eventoAtualizado);

        verify(delegado).atualizar(eventoAtualizado);
    }
}
