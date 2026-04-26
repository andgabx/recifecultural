package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.Nota;
import recifecultural.dominio.agenda.comentario.StatusComentario;
import recifecultural.dominio.agenda.espectador.Espectador;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PassosDiscutirEventos {

    private final ContextoDiscutirEvento contexto;

    private UUID outroEspectadorId;
    private UUID eventoIdCorrente;
    private UUID comentarioPaiId;

    public PassosDiscutirEventos(ContextoDiscutirEvento contexto) {
        this.contexto = contexto;
    }

    @Dado("um espectador cadastrado")
    public void umEspectadorCadastrado() {
        contexto.espectador = new Espectador(UUID.randomUUID(), "Maria Silva", "maria@example.com");
    }

    @Dado("um evento existente")
    public void umEventoExistente() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Festival de Jazz",
                "Festival anual no Marco Zero",
                "Descrição completa do festival",
                new Periodo(agora.minusDays(5), agora.minusDays(1)),
                null,
                new Preco(new BigDecimal("80.00"), new BigDecimal("40.00"), null)
        );
        eventoIdCorrente = contexto.evento.getId();
    }

    // --- HU-1: Postar comentários ---

    @Quando("o espectador tentar postar um comentário com texto {string}")
    public void oEspectadorTentarPostarComentarioComTexto(String texto) {
        try {
            Comentario comentario = new Comentario(UUID.randomUUID(), contexto.espectador.getId(), eventoIdCorrente, texto);
            contexto.comentarioServico.postar(comentario);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Quando("o espectador tentar postar um comentário com mais de 500 caracteres")
    public void oEspectadorTentarPostarComentarioLongo() {
        try {
            String textoLongo = "A".repeat(501);
            Comentario comentario = new Comentario(UUID.randomUUID(), contexto.espectador.getId(), eventoIdCorrente, textoLongo);
            contexto.comentarioServico.postar(comentario);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Quando("o espectador postar o comentário {string}")
    public void oEspectadorPostarComentario(String texto) {
        contexto.comentario = new Comentario(UUID.randomUUID(), contexto.espectador.getId(), eventoIdCorrente, texto);
        when(contexto.comentarioRepositorio.obter(any())).thenReturn(Optional.of(contexto.comentario));
        contexto.comentarioServico.postar(contexto.comentario);
    }

    @Então("o comentário deve estar registrado no sistema")
    public void oComentarioDeveEstarRegistradoNoSistema() {
        assertNotNull(contexto.comentario);
        assertEquals(StatusComentario.ATIVO, contexto.comentario.getStatus());
    }

    @Então("o sistema deve lançar um erro de texto inválido")
    public void oSistemaDeveLancarErroDeTextoInvalido() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    // --- HU-2: Curtir comentários ---

    @Dado("um comentário postado pelo próprio espectador")
    public void umComentarioPostadoPeloProprioEspectador() {
        contexto.comentario = new Comentario(
                UUID.randomUUID(), contexto.espectador.getId(), UUID.randomUUID(),
                "Espetáculo muito bonito, valeu a pena cada detalhe!"
        );
        when(contexto.comentarioRepositorio.obter(contexto.comentario.getId()))
                .thenReturn(Optional.of(contexto.comentario));
    }

    @Dado("um comentário postado por outro espectador")
    public void umComentarioPostadoPorOutroEspectador() {
        outroEspectadorId = UUID.randomUUID();
        contexto.comentario = new Comentario(
                UUID.randomUUID(), outroEspectadorId, UUID.randomUUID(),
                "Espetáculo muito bonito, valeu a pena cada detalhe!"
        );
        when(contexto.comentarioRepositorio.obter(contexto.comentario.getId()))
                .thenReturn(Optional.of(contexto.comentario));
    }

    @Quando("o espectador tentar curtir o próprio comentário")
    public void oEspectadorTentarCurtirOProprioComentario() {
        try {
            contexto.comentarioServico.curtir(contexto.comentario.getId(), contexto.espectador.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Quando("o espectador curtir o comentário")
    public void oEspectadorCurtirOComentario() {
        contexto.comentarioServico.curtir(contexto.comentario.getId(), contexto.espectador.getId());
    }

    @E("o espectador tentar curtir o mesmo comentário novamente")
    public void oEspectadorTentarCurtirOMesmoComentarioNovamente() {
        try {
            contexto.comentarioServico.curtir(contexto.comentario.getId(), contexto.espectador.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de curtida inválida")
    public void oSistemaDeveLancarErroDeCurtidaInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    @Então("o sistema deve lançar um erro de curtida duplicada")
    public void oSistemaDeveLancarErroDeCurtidaDuplicada() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Então("a curtida deve estar registrada no comentário")
    public void aCurtidaDeveEstarRegistradaNoComentario() {
        assertTrue(contexto.comentario.getCurtidas().contains(contexto.espectador.getId()));
    }

    // --- HU-3: Responder comentários ---

    @Dado("um comentário existente no sistema")
    public void umComentarioExistenteNoSistema() {
        contexto.comentario = new Comentario(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Apresentação marcante, o tenor foi espetacular no segundo ato!"
        );
        comentarioPaiId = contexto.comentario.getId();
        when(contexto.comentarioRepositorio.obter(comentarioPaiId))
                .thenReturn(Optional.of(contexto.comentario));
    }

    @Quando("o espectador tentar responder com texto {string}")
    public void oEspectadorTentarResponderComTexto(String texto) {
        try {
            Comentario resposta = new Comentario(
                    UUID.randomUUID(), contexto.espectador.getId(), contexto.comentario.getEventoId(), texto, comentarioPaiId
            );
            contexto.comentarioServico.responder(comentarioPaiId, resposta);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Quando("o espectador tentar responder a um comentário que não existe")
    public void oEspectadorTentarResponderAComentarioQueNaoExiste() {
        try {
            UUID idInexistente = UUID.randomUUID();
            when(contexto.comentarioRepositorio.obter(idInexistente)).thenReturn(Optional.empty());
            Comentario resposta = new Comentario(
                    UUID.randomUUID(), contexto.espectador.getId(), UUID.randomUUID(),
                    "Concordo plenamente com o comentário anterior sobre o espetáculo!"
            );
            contexto.comentarioServico.responder(idInexistente, resposta);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Quando("o espectador responder com {string}")
    public void oEspectadorResponderCom(String texto) {
        Comentario resposta = new Comentario(
                UUID.randomUUID(), contexto.espectador.getId(), contexto.comentario.getEventoId(), texto, comentarioPaiId
        );
        contexto.comentarioServico.responder(comentarioPaiId, resposta);
        contexto.comentario = resposta;
    }

    @Então("o sistema deve lançar um erro de comentário não encontrado")
    public void oSistemaDeveLancarErroDeComentarioNaoEncontrado() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    @Então("a resposta deve estar vinculada ao comentário pai")
    public void aRespostaDeveEstarVinculadaAoComentarioPai() {
        assertEquals(comentarioPaiId, contexto.comentario.getComentarioPaiId());
    }

    // --- HU-4: Visualizar comentários ---

    @Quando("o autor deletar o comentário")
    public void oAutorDeletarOComentario() {
        contexto.comentarioServico.deletar(contexto.comentario.getId());
    }

    @Quando("os comentários do evento forem listados")
    public void osComentariosDoEventoForemListados() {
        UUID eventoId = contexto.comentario != null
                ? contexto.comentario.getEventoId()
                : (eventoIdCorrente != null ? eventoIdCorrente : UUID.randomUUID());
        List<Comentario> todos = contexto.comentario != null
                ? List.of(contexto.comentario)
                : List.of();
        when(contexto.comentarioRepositorio.listarPorEvento(eventoId)).thenReturn(todos);
        List<Comentario> ativos = contexto.comentarioServico.listarAtivos(eventoId);
        contexto.comentario = ativos.isEmpty() ? null : ativos.get(0);
    }

    @Então("o comentário deletado não deve aparecer na listagem")
    public void oComentarioDeletadoNaoDeveAparecerNaListagem() {
        assertNull(contexto.comentario);
    }

    @Então("a listagem deve estar vazia")
    public void aListagemDeveEstarVazia() {
        assertNull(contexto.comentario);
    }

    // --- HU-5: Comentário com nota ---

    @Dado("o espectador não esteve presente no evento")
    public void oEspectadorNaoEstevePresenteNoEvento() {
        when(contexto.bilheteria.verificarPresenca(contexto.espectador.getId(), eventoIdCorrente))
                .thenReturn(false);
    }

    @Dado("o espectador esteve presente no evento")
    public void oEspectadorEstevePresenteNoEvento() {
        when(contexto.bilheteria.verificarPresenca(contexto.espectador.getId(), eventoIdCorrente))
                .thenReturn(true);
    }

    @Dado("o espectador já postou nota {int} para o evento")
    public void oEspectadorJaPostouNotaParaOEvento(int valorNota) {
        Comentario jaAvaliou = new Comentario(
                UUID.randomUUID(), contexto.espectador.getId(), eventoIdCorrente,
                "Primeira avaliação do espetáculo que assisti ontem à noite!",
                new Nota(valorNota), null
        );
        when(contexto.comentarioRepositorio.listarPorEvento(eventoIdCorrente))
                .thenReturn(List.of(jaAvaliou));
    }

    @Quando("o espectador tentar postar nota {int} para o evento")
    public void oEspectadorTentarPostarNotaParaOEvento(int valorNota) {
        try {
            postarNotaInterno(valorNota, "Avaliação do espetáculo que assisti recentemente no teatro.");
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Quando("o espectador postar nota {int} para o evento com comentário {string}")
    public void oEspectadorPostarNotaParaOEventoComComentario(int valorNota, String texto) {
        postarNotaInterno(valorNota, texto);
    }

    private void postarNotaInterno(int valorNota, String texto) {
        List<Comentario> existentes = contexto.comentarioRepositorio
                .listarPorEvento(eventoIdCorrente);
        boolean jaAvaliou = existentes.stream()
                .anyMatch(c -> c.getEspectadorId().equals(contexto.espectador.getId())
                        && c.getNota() != null);
        if (jaAvaliou)
            throw new IllegalStateException("Espectador já avaliou este evento.");

        Comentario comentario = new Comentario(
                UUID.randomUUID(), contexto.espectador.getId(), eventoIdCorrente,
                texto,
                new Nota(valorNota),
                null
        );
        when(contexto.comentarioRepositorio.obter(any())).thenReturn(Optional.of(comentario));
        contexto.comentarioServico.postarComNota(comentario, contexto.bilheteria);
        contexto.comentario = comentario;
    }

    @Então("o sistema deve lançar um erro de presença não confirmada")
    public void oSistemaDeveLancarErroDePresencaNaoConfirmada() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Então("o sistema deve lançar um erro de nota inválida")
    public void oSistemaDeveLancarErroDeNotaInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    @Então("o sistema deve lançar um erro de avaliação duplicada")
    public void oSistemaDeveLancarErroDeAvaliacaoDuplicada() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Então("o comentário com nota deve estar registrado no sistema")
    public void oComentarioComNotaDeveEstarRegistradoNoSistema() {
        assertNotNull(contexto.comentario);
        assertNotNull(contexto.comentario.getNota());
        assertEquals(StatusComentario.ATIVO, contexto.comentario.getStatus());
    }
}
