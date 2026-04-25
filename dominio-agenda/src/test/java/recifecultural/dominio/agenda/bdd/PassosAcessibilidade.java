package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidade;
import recifecultural.dominio.agenda.acessibilidade.StatusRecurso;
import recifecultural.dominio.agenda.acessibilidade.TipoRecursoAcessibilidade;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PassosAcessibilidade {

    private final ContextoAcessibilidade contexto;

    public PassosAcessibilidade(ContextoAcessibilidade contexto) {
        this.contexto = contexto;
    }

    @Dado("uma apresentação de evento em análise")
    public void umaApresentacaoDeEventoEmAnalise() {
        contexto.evento = criarEventoBase();
        contexto.evento.submeterParaAnalise();
        contexto.apresentacaoId = UUID.randomUUID();
        when(contexto.eventoRepositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
        when(contexto.recursoRepositorio.listarPorApresentacao(any())).thenReturn(contexto.recursosDaApresentacao);
    }

    @Dado("uma apresentação de evento aprovado")
    public void umaApresentacaoDeEventoAprovado() {
        contexto.evento = criarEventoAprovado();
        contexto.apresentacaoId = UUID.randomUUID();
        when(contexto.eventoRepositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
        when(contexto.recursoRepositorio.listarPorApresentacao(any())).thenReturn(contexto.recursosDaApresentacao);
    }

    @Dado("uma apresentação de evento aprovado com o recurso {string} já marcado")
    public void umaApresentacaoComRecursoJaMarcado(String tipo) {
        umaApresentacaoDeEventoAprovado();
        contexto.recurso = contexto.servico.marcar(
                contexto.apresentacaoId,
                contexto.evento.getId(),
                TipoRecursoAcessibilidade.valueOf(tipo)
        );
        contexto.recursosDaApresentacao.add(contexto.recurso);
    }

    @Quando("o promotor marcar o recurso {string}")
    public void oPromotorMarcarORecurso(String tipo) {
        contexto.recurso = contexto.servico.marcar(
                contexto.apresentacaoId,
                contexto.evento.getId(),
                TipoRecursoAcessibilidade.valueOf(tipo)
        );
    }

    @Quando("o promotor tentar marcar o recurso {string}")
    public void oPromotorTentarMarcarORecurso(String tipo) {
        try {
            contexto.recurso = contexto.servico.marcar(
                    contexto.apresentacaoId,
                    contexto.evento.getId(),
                    TipoRecursoAcessibilidade.valueOf(tipo)
            );
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o recurso deve estar com status {string}")
    public void oRecursoDeveEstarComStatus(String esperado) {
        assertEquals(StatusRecurso.valueOf(esperado), contexto.recurso.getStatus());
    }

    @Então("o sistema deve lançar um erro de regra de acessibilidade")
    public void oSistemaDeveLancarErroDeRegraDeAcessibilidade() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Dado("uma apresentação com o recurso {string} confirmado")
    public void umaApresentacaoComRecursoConfirmado(String tipo) {
        umaApresentacaoDeEventoAprovado();
        contexto.recurso = contexto.servico.marcar(
                contexto.apresentacaoId,
                contexto.evento.getId(),
                TipoRecursoAcessibilidade.valueOf(tipo)
        );
        when(contexto.recursoRepositorio.obter(any())).thenReturn(Optional.of(contexto.recurso));
    }

    @Quando("o gestor tentar remover o recurso sem justificativa")
    public void oGestorTentarRemoverORecursoSemJustificativa() {
        try {
            contexto.servico.remover(contexto.recurso.getId(), "");
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Quando("o gestor tentar remover o recurso com a justificativa {string}")
    public void oGestorTentarRemoverORecursoComJustificativa(String justificativa) {
        try {
            contexto.servico.remover(contexto.recurso.getId(), justificativa);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de justificativa inválida")
    public void oSistemaDeveLancarErroDeJustificativaInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    @Quando("o gestor remover o recurso com a justificativa {string}")
    public void oGestorRemoverORecursoComJustificativa(String justificativa) {
        contexto.servico.remover(contexto.recurso.getId(), justificativa);
    }

    @E("a justificativa de remoção deve estar registrada")
    public void aJustificativaDeRemocaoDeveEstarRegistrada() {
        assertNotNull(contexto.recurso.getJustificativaRemocao());
    }

    private Evento criarEventoBase() {
        LocalDateTime agora = LocalDateTime.now();
        Evento evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Hamlet em Libras",
                "Adaptação acessível",
                "Espetáculo com tradução para Libras",
                new Periodo(agora.plusDays(10), agora.plusDays(40)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        evento.programarApresentacao(agora.plusDays(20));
        return evento;
    }

    private Evento criarEventoAprovado() {
        Evento evento = criarEventoBase();
        evento.submeterParaAnalise();
        evento.aprovar();
        return evento;
    }
}
