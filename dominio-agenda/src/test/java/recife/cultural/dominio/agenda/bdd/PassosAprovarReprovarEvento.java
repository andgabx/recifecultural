package recife.cultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.FeedbackReprovacao;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;
import recifecultural.dominio.agenda.evento.StatusEvento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PassosAprovarReprovarEvento {

    private final ContextoCenario contexto;

    public PassosAprovarReprovarEvento(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    // === HU-1: Gestor aprova evento em analise ===

    @Dado("um evento submetido para análise")
    public void umEventoSubmetidoParaAnalise() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(),
                "Peca de Teatro Classico",
                "Apresentacao no Parque Dona Lindu",
                "Descricao longa do espetaculo",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("40.00"), new BigDecimal("20.00"), null)
        );
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servico.salvar(contexto.evento);
        contexto.servico.submeterParaAnalise(contexto.evento.getId());
    }

    @Quando("o gestor aprovar o evento")
    public void oGestorAprovarOEvento() {
        contexto.servico.aprovar(contexto.evento.getId());
    }

    @Então("o status do evento deve ser {string}")
    public void oStatusDoEventoDeveSer(String statusEsperado) {
        assertEquals(StatusEvento.valueOf(statusEsperado), contexto.evento.getStatus());
        verify(contexto.repositorio, atLeastOnce()).atualizar(contexto.evento);
    }

    // === HU-1: Gestor tenta aprovar evento ja aprovado ===

    @Dado("um evento já aprovado")
    public void umEventoJaAprovado() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(),
                "Exposicao de Fotografia",
                "Mostra de fotografos pernambucanos",
                "Descricao longa da exposicao",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("20.00"), new BigDecimal("10.00"), null)
        );
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servico.salvar(contexto.evento);
        contexto.servico.submeterParaAnalise(contexto.evento.getId());
        contexto.servico.aprovar(contexto.evento.getId());
    }

    @Quando("o gestor tentar aprovar o evento novamente")
    public void oGestorTentarAprovarOEventoNovamente() {
        try {
            contexto.servico.aprovar(contexto.evento.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de transição de status inválida")
    public void oSistemaDeveLancarErroDeTransicaoDeStatusInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    // === HU-2: Gestor tenta reprovar com feedback vazio ===
    // (Dado: reutiliza "um evento submetido para analise")

    @Quando("o gestor tentar reprovar o evento com feedback vazio")
    public void oGestorTentarReprovarOEventoComFeedbackVazio() {
        try {
            contexto.servico.reprovar(contexto.evento.getId(), new FeedbackReprovacao(""));
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de feedback inválido")
    public void oSistemaDeveLancarErroFeedbackInvalido() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    // === HU-2: Gestor reprova com feedback valido ===
    // (Dado: reutiliza "um evento submetido para analise")
    // (Entao: reutiliza "o status do evento deve ser {string}")

    @Quando("o gestor reprovar o evento com feedback {string}")
    public void oGestorReprovarOEventoComFeedback(String textoFeedback) {
        contexto.servico.reprovar(contexto.evento.getId(), new FeedbackReprovacao(textoFeedback));
    }

    @E("o feedback de reprovação deve estar registrado no evento")
    public void oFeedbackDeReprovacaoDeveEstarRegistradoNoEvento() {
        assertNotNull(contexto.evento.getFeedbackReprovacao());
    }

    // === HU-2: Gestor tenta reprovar apos prazo de 30 dias em analise ===

    @Dado("um evento submetido para análise há mais de 30 dias")
    public void umEventoSubmetidoParaAnaliseHaMaisDe30Dias() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(),
                "Festival de Cinema Pernambucano",
                "Mostra anual de curtametragens locais",
                "Descricao longa do festival",
                new Periodo(agora.minusDays(40), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("30.00"), new BigDecimal("15.00"), null)
        );
        contexto.evento.programarApresentacao(agora.minusDays(35));
        // Simula estado vindo do banco: evento foi submetido ha 31 dias
        contexto.evento.submeterParaAnalise(agora.minusDays(31));
        when(contexto.repositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
    }

    @Quando("o gestor tentar reprovar o evento com feedback {string}")
    public void oGestorTentarReprovarOEventoComFeedback(String textoFeedback) {
        try {
            contexto.servico.reprovar(contexto.evento.getId(), new FeedbackReprovacao(textoFeedback));
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de prazo de reprovação expirado")
    public void oSistemaDeveLancarErroDePrazoDeReprovacaoExpirado() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains("Prazo de reprovação"));
    }

    // === HU-3: Promotor tenta submeter sem apresentacoes ===

    @Dado("um evento cadastrado sem datas de apresentação")
    public void umEventoCadastradoSemDatasDeApresentacao() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz no Marco Zero",
                "Show ao vivo com artistas locais",
                "Descricao longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        when(contexto.repositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servico.salvar(contexto.evento);
    }

    @Quando("o promotor tentar submeter o evento para análise")
    public void oPromotorTentarSubmeterOEventoParaAnalise() {
        try {
            contexto.servico.submeterParaAnalise(contexto.evento.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de submissão inválida")
    public void oSistemaDeveLancarErroDeSubmissaoInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    // === HU-3: Promotor submete com apresentacao ===
    // (Entao: reutiliza "o status do evento deve ser {string}")

    @Dado("um evento cadastrado com uma data de apresentação programada")
    public void umEventoCadastradoComUmaDataDeApresentacaoProgramada() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz no Marco Zero",
                "Show ao vivo com artistas locais",
                "Descricao longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servico.salvar(contexto.evento);
    }

    @Quando("o promotor submeter o evento para análise")
    public void oPromotorSubmeterOEventoParaAnalise() {
        contexto.servico.submeterParaAnalise(contexto.evento.getId());
    }
}
