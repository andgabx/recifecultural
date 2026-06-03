package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.ArgumentCaptor;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;
import recifecultural.dominio.agenda.evento.StatusEvento;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PassosBloqueioAdministrativo {

    private final ContextoCenario contexto;
    private List<Evento> eventosSimuladosNoBanco = new ArrayList<>();

    private BloqueioAdministrativoId idBloqueioAtual;
    private BloqueioAdministrativo bloqueioRetornado;

    public PassosBloqueioAdministrativo(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Dado("que existe um local com ID {string}")
    public void queExisteUmLocalComID(String idLocal) {
        contexto.idLocalAtual = UUID.fromString(idLocal);
        contexto.idEspacoAtual = new EspacoId(contexto.idLocalAtual);

        Espaco espacoMock = mock(Espaco.class);
        when(espacoMock.getId()).thenReturn(contexto.idEspacoAtual);
        when(contexto.repositorioEspaco.obterPorId(contexto.idEspacoAtual)).thenReturn(Optional.of(espacoMock));

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
        LocalDate inicio = LocalDate.parse("2024-05-01");
        LocalDate fim = LocalDate.parse("2024-05-15");

        try {
            contexto.servicoBloqueio.criarBloqueio(contexto.idEspacoAtual, inicio, fim, motivo);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o bloqueio deve ser salvo com sucesso")
    public void oBloqueioDeveSerSalvoComSucesso() {
        assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção: " + (contexto.excecaoCapturada != null ? contexto.excecaoCapturada.getMessage() : ""));
        verify(contexto.repositorioBloqueio, times(1)).salvar(any(BloqueioAdministrativo.class));
    }

    @E("nenhum evento deve ser cancelado")
    public void nenhumEventoDeveSerCancelado() {
        verify(contexto.repositorioEvento, never()).atualizar(any(Evento.class));
    }

    @E("existem eventos agendados para este local entre {string} e {string}")
    public void existemEventosAgendadosParaEsteLocalEntreE(String inicioStr, String fimStr) {
        LocalDate inicio = LocalDate.parse(inicioStr);
        LocalDate fim = LocalDate.parse(fimStr);

        Evento eventoConflitante = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), contexto.idLocalAtual, "Show", "Curta", "Longa",
                new Periodo(inicio.atStartOfDay().plusDays(1), fim.atStartOfDay().minusDays(1)), null,
                new Preco(BigDecimal.TEN, BigDecimal.ONE, null)
        );

        eventoConflitante.adicionarArtista(UUID.randomUUID());
        eventoConflitante.definirCategoria("Música");
        eventoConflitante.programarApresentacao(inicio.atStartOfDay().plusDays(2));
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

    @E("os promotores dos eventos cancelados devem receber uma notificação com o contexto {string}")
    public void osPromotoresDosEventosCanceladosDevemReceberUmaNotificacaoComOContexto(String contextoEsperado) {
        ArgumentCaptor<Notificacao> captor = ArgumentCaptor.forClass(Notificacao.class);
        verify(contexto.repositorioNotificacao, atLeastOnce()).salvar(captor.capture());

        List<Notificacao> notificacoesSalvas = captor.getAllValues();
        assertFalse(notificacoesSalvas.isEmpty(), "Nenhuma notificação foi salva.");

        for (Evento evento : eventosSimuladosNoBanco) {
            boolean notificacaoEncontrada = notificacoesSalvas.stream().anyMatch(n ->
                    contextoEsperado.equals(n.getContexto()) &&
                            evento.getPromotorId().equals(n.getUsuarioAlvo())
            );
            assertTrue(notificacaoEncontrada, "Faltou a notificação com contexto e referência correta para o evento " + evento.getId());
        }
    }

    @Quando("eu solicitar a criação de um bloqueio administrativo para este local de {string} até {string} com o motivo {string}")
    public void euSolicitarACriacaoDeUmBloqueioDeAteComOMotivo(String inicioStr, String fimStr, String motivo) {
        LocalDate inicio = LocalDate.parse(inicioStr);
        LocalDate fim = LocalDate.parse(fimStr);

        try {
            contexto.servicoBloqueio.criarBloqueio(contexto.idEspacoAtual, inicio, fim, motivo);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve retornar um erro de validação informando que a data de fim é antes do início")
    public void oSistemaDeveRetornarErroDeValidacaoDataFimAntesDoInicio() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains("anterior à data inicial"));
    }

    @Quando("eu solicitar a criação de um bloqueio administrativo para este local de {string} até {string} sem informar o motivo")
    public void euSolicitarACriacaoDeUmBloqueioSemInformarOMotivo(String inicioStr, String fimStr) {
        LocalDate inicio = LocalDate.parse(inicioStr);
        LocalDate fim = LocalDate.parse(fimStr);

        try {
            contexto.servicoBloqueio.criarBloqueio(contexto.idEspacoAtual, inicio, fim, "");
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve retornar um erro de validação informando que o motivo é obrigatório")
    public void oSistemaDeveRetornarErroDeValidacaoMotivoObrigatorio() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains("Justificativa obrigatória"));
    }

    @Então("o sistema deve retornar um erro de validação informando que a justificativa deve conter no mínimo {int} caracteres")
    public void oSistemaDeveRetornarErroDeValidacaoJustificativaMinima(int minChars) {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains("mínimo 10 caracteres"));
    }

    @Dado("que existe um bloqueio salvo no repositório com ID {string} e motivo {string}")
    public void queExisteUmBloqueioSalvoNoRepositorioComIDE(String idStr, String motivo) {
        idBloqueioAtual = BloqueioAdministrativoId.de(idStr);
        contexto.idEspacoAtual = EspacoId.novo();
        BloqueioAdministrativo bloqueioMock = new BloqueioAdministrativo(
                contexto.idEspacoAtual, LocalDate.now().plusDays(1), LocalDate.now().plusDays(5), motivo
        );

        BloqueioAdministrativo spy = spy(bloqueioMock);
        doReturn(idBloqueioAtual).when(spy).getId();

        when(contexto.repositorioBloqueio.obter(idBloqueioAtual)).thenReturn(spy);

        Espaco espacoMock = mock(Espaco.class);
        when(contexto.repositorioEspaco.obterPorId(contexto.idEspacoAtual)).thenReturn(Optional.of(espacoMock));
    }

    @Dado("que existe um bloqueio salvo no repositório com ID {string}")
    public void queExisteUmBloqueioSalvoNoRepositorioComID(String idStr) {
        queExisteUmBloqueioSalvoNoRepositorioComIDE(idStr, "Motivo Padrão Qualquer");
    }

    @Quando("eu solicitar a busca deste bloqueio por ID")
    public void euSolicitarABuscaDesteBloqueioPorID() {
        try {
            bloqueioRetornado = contexto.servicoBloqueio.obterPorId(idBloqueioAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve retornar o bloqueio com motivo {string}")
    public void oSistemaDeveRetornarOBloqueioComMotivo(String motivoEsperado) {
        assertNull(contexto.excecaoCapturada);
        assertNotNull(bloqueioRetornado);
        assertEquals(motivoEsperado, bloqueioRetornado.getJustificativa());
    }

    @Quando("eu solicitar a atualização deste bloqueio para o motivo {string} de {string} até {string}")
    public void euSolicitarAAtualizacaoDesteBloqueioParaOMotivoDeAte(String novoMotivo, String inicioStr, String fimStr) {
        LocalDate novoInicio = LocalDate.parse(inicioStr);
        LocalDate novoFim = LocalDate.parse(fimStr);
        try {
            contexto.servicoBloqueio.atualizarBloqueio(idBloqueioAtual, novoMotivo, novoInicio, novoFim);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o bloqueio deve ser atualizado com sucesso")
    public void oBloqueioDeveSerAtualizadoComSucesso() {
        assertNull(contexto.excecaoCapturada);
        verify(contexto.repositorioBloqueio, times(1)).atualizar(any(BloqueioAdministrativo.class));
    }

    @E("possíveis eventos conflitantes no novo período devem ser verificados para cancelamento")
    public void possiveisEventosConflitantesNoNovoPeriodoDevemSerCancelados() {
        verify(contexto.repositorioEvento, atLeastOnce()).obterPorLocalEIntervalo(any(), any(), any());
    }

    @Quando("eu solicitar a exclusão deste bloqueio")
    public void euSolicitarAExclusaoDesteBloqueio() {
        try {
            contexto.servicoBloqueio.deletarBloqueio(idBloqueioAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o bloqueio deve ser removido do repositório")
    public void oBloqueioDeveSerRemovidoDoRepositorio() {
        assertNull(contexto.excecaoCapturada);
        verify(contexto.repositorioBloqueio, times(1)).deletar(idBloqueioAtual);
    }

    @Quando("eu solicitar a desativação deste bloqueio")
    public void euSolicitarADesativacaoDesteBloqueio() {
        try {
            contexto.servicoBloqueio.desativarBloqueio(idBloqueioAtual, false);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o bloqueio deve constar como inativo")
    public void oBloqueioDeveConstarComoInativo() {
        assertNull(contexto.excecaoCapturada);
        verify(contexto.repositorioBloqueio, times(1)).atualizar(any(BloqueioAdministrativo.class));
        BloqueioAdministrativo spy = contexto.repositorioBloqueio.obter(idBloqueioAtual);
        assertFalse(spy.isAtivo(), "O bloqueio deveria estar inativo");
    }
}