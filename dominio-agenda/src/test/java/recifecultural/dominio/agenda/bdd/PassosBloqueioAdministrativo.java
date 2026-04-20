package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;
import recifecultural.dominio.agenda.evento.StatusEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PassosBloqueioAdministrativo {

    private final ContextoCenario contexto;
    private List<Evento> eventosSimuladosNoBanco = new ArrayList<>();

    public PassosBloqueioAdministrativo(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Dado("que existe um local com ID {string}")
    public void queExisteUmLocalComID(String idLocal) {
        contexto.idLocalAtual = UUID.fromString(idLocal);
        contexto.excecaoCapturada = null;
    }

    @E("não existem eventos agendados para este local entre {string} e {string}")
    public void naoExistemEventosAgendadosParaEsteLocalEntreE(String dataInicio, String dataFim) {
        when(contexto.repositorioEvento.obterPorLocalEIntervalo(
                eq(contexto.idLocalAtual),
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
    }

    @Quando("eu solicitar a criação de um bloqueio administrativo para este local neste período com o motivo {string}")
    public void euSolicitarACriacaoDeUmBloqueioAdministrativoParaEsteLocalNestePeriodoComOMotivo(String motivo) {
        LocalDateTime inicio = LocalDateTime.parse("2024-05-01T08:00:00");
        LocalDateTime fim = LocalDateTime.parse("2024-05-15T18:00:00");

        try {
            BloqueioAdministrativo bloqueio = new BloqueioAdministrativo(contexto.idLocalAtual, motivo, inicio, fim);
            contexto.servicoBloqueio.criarBloqueio(bloqueio);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o bloqueio deve ser salvo com sucesso")
    public void oBloqueioDeveSerSalvoComSucesso() {
        assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção");
        verify(contexto.repositorioBloqueio, times(1)).salvar(any(BloqueioAdministrativo.class));
    }

    @E("nenhum evento deve ser cancelado")
    public void nenhumEventoDeveSerCancelado() {
        verify(contexto.repositorioEvento, never()).atualizar(any(Evento.class));
    }

    @E("existem eventos agendados para este local entre {string} e {string}")
    public void existemEventosAgendadosParaEsteLocalEntreE(String inicioStr, String fimStr) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);

        Evento eventoConflitante = new Evento(
                UUID.randomUUID(), contexto.idLocalAtual, "Show", "Curta", "Longa",
                new Periodo(inicio.plusDays(1), fim.minusDays(1)), null,
                new Preco(BigDecimal.TEN, BigDecimal.ONE, null)
        );

        eventoConflitante.programarApresentacao(inicio.plusDays(2));

        eventoConflitante.submeterParaAnalise();
        eventoConflitante.aprovar();

        eventosSimuladosNoBanco.add(eventoConflitante);

        when(contexto.repositorioEvento.obterPorLocalEIntervalo(
                eq(contexto.idLocalAtual), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(eventosSimuladosNoBanco);
    }

    @Então("os eventos conflitantes devem ser cancelados")
    public void osEventosConflitantesDevemSerCancelados() {
        for (Evento evento : eventosSimuladosNoBanco) {
            assertEquals(StatusEvento.CANCELADO, evento.getStatus());
            assertNotNull(evento.getMotivoCancelamento());
            assertTrue(evento.getMotivoCancelamento().contains("bloqueio administrativo"));
            verify(contexto.repositorioEvento, atLeastOnce()).atualizar(evento);
        }
    }

    @Quando("eu solicitar a criação de um bloqueio administrativo para este local de {string} até {string} com o motivo {string}")
    public void euSolicitarACriacaoDeUmBloqueioDeAteComOMotivo(String inicioStr, String fimStr, String motivo) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);

        try {
            BloqueioAdministrativo bloqueio = new BloqueioAdministrativo(contexto.idLocalAtual, motivo, inicio, fim);
            contexto.servicoBloqueio.criarBloqueio(bloqueio);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve retornar um erro de validação informando que a data de fim é antes do início")
    public void oSistemaDeveRetornarErroDeValidacaoDataFimAntesDoInicio() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains("Fim antes do início"));
    }

    @Quando("eu solicitar a criação de um bloqueio administrativo para este local de {string} até {string} sem informar o motivo")
    public void euSolicitarACriacaoDeUmBloqueioSemInformarOMotivo(String inicioStr, String fimStr) {
        LocalDateTime inicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fim = LocalDateTime.parse(fimStr);

        try {
            BloqueioAdministrativo bloqueio = new BloqueioAdministrativo(contexto.idLocalAtual, "", inicio, fim);
            contexto.servicoBloqueio.criarBloqueio(bloqueio);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve retornar um erro de validação informando que o motivo é obrigatório")
    public void oSistemaDeveRetornarErroDeValidacaoMotivoObrigatorio() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains("Motivo é obrigatório"));
    }
}