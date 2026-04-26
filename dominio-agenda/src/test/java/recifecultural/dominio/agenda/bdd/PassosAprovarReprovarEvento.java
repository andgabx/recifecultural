package recifecultural.dominio.agenda.bdd;

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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PassosAprovarReprovarEvento {

    private final ContextoAprovarReprovarEvento contexto;

    public PassosAprovarReprovarEvento(ContextoAprovarReprovarEvento contexto) {
        this.contexto = contexto;
    }

    @Dado("um evento submetido para análise")
    public void umEventoSubmetidoParaAnalise() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Peça de Teatro Clássico",
                "Apresentação no Parque Dona Lindu",
                "Descrição longa do espetáculo",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("40.00"), new BigDecimal("20.00"), null)
        );
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
    }

    @Então("o status do evento deve ser {string}")
    public void oStatusDoEventoDeveSer(String statusEsperado) {
        assertEquals(StatusEvento.valueOf(statusEsperado), contexto.evento.getStatus());
    }

    @Quando("o gestor aprovar o evento")
    public void oGestorAprovarOEvento() {
        contexto.servicoEvento.aprovar(contexto.evento.getId());
    }

    @Dado("um evento já aprovado")
    public void umEventoJaAprovado() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Exposição de Fotografia",
                "Mostra de fotógrafos pernambucanos",
                "Descrição longa da exposição",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("20.00"), new BigDecimal("10.00"), null)
        );
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
        contexto.servicoEvento.aprovar(contexto.evento.getId());
    }

    @Quando("o gestor tentar aprovar o evento novamente")
    public void oGestorTentarAprovarOEventoNovamente() {
        try {
            contexto.servicoEvento.aprovar(contexto.evento.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de transição de status inválida")
    public void oSistemaDeveLancarErroDeTransicaoDeStatusInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Quando("o gestor tentar reprovar o evento com feedback vazio")
    public void oGestorTentarReprovarOEventoComFeedbackVazio() {
        try {
            contexto.servicoEvento.reprovar(contexto.evento.getId(), new FeedbackReprovacao(""));
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de feedback inválido")
    public void oSistemaDeveLancarErroFeedbackInvalido() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    @Quando("o gestor reprovar o evento com feedback {string}")
    public void oGestorReprovarOEventoComFeedback(String textoFeedback) {
        contexto.servicoEvento.reprovar(contexto.evento.getId(), new FeedbackReprovacao(textoFeedback));
    }

    @E("o feedback de reprovação deve estar registrado no evento")
    public void oFeedbackDeReprovacaoDeveEstarRegistradoNoEvento() {
        assertNotNull(contexto.evento.getFeedbackReprovacao());
    }

    @Dado("um evento cadastrado sem datas de apresentação")
    public void umEventoCadastradoSemDatasDeApresentacao() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz no Marco Zero",
                "Show ao vivo com artistas locais",
                "Descrição longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    @Quando("o promotor tentar submeter o evento para análise")
    public void oPromotorTentarSubmeterOEventoParaAnalise() {
        try {
            contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de submissão inválida")
    public void oSistemaDeveLancarErroDeSubmissaoInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Dado("um evento cadastrado com uma data de apresentação programada")
    public void umEventoCadastradoComUmaDataDeApresentacaoProgramada() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz no Marco Zero",
                "Show ao vivo com artistas locais",
                "Descrição longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    @Quando("o promotor submeter o evento para análise")
    public void oPromotorSubmeterOEventoParaAnalise() {
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
    }
}
