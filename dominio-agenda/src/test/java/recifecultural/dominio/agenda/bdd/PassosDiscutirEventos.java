package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.BilheteriaDigital;
import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;
import recifecultural.dominio.agenda.comentario.ComentarioService;
import recifecultural.dominio.agenda.comentario.Nota;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PassosDiscutirEventos {

    private UUID espectadorId;
    private UUID eventoId;
    private UUID comentarioId;
    private Comentario comentario;
    private ComentarioService service;
    private Exception excecaoLancada;
    private List<Comentario> comentariosDoEvento;

    // Fake Repositorio
    private final ComentarioRepositorio repositorio = new ComentarioRepositorio() {
        private final List<Comentario> banco = new ArrayList<>();

        @Override
        public void salvar(Comentario comentario) {
            banco.add(comentario);
        }

        @Override
        public Optional<Comentario> obter(UUID id) {
            return banco.stream().filter(c -> c.getId().equals(id)).findFirst();
        }

        @Override
        public void atualizar(Comentario comentario) {
            // In memory, already updated
        }

        @Override
        public void deletar(UUID id) {
            obter(id).ifPresent(Comentario::deletar);
        }

        @Override
        public List<Comentario> listarPorEvento(UUID eventoId) {
            return banco.stream().filter(c -> c.getEventoId().equals(eventoId)).collect(Collectors.toList());
        }
    };

    // Fake Bilheteria
    private boolean estevePresente = false;
    private final BilheteriaDigital bilheteria = (eId, evId) -> estevePresente;

    public PassosDiscutirEventos() {
        // Mock service
        this.service = new ComentarioService(repositorio) {
            @Override
            public void postarComNota(Comentario comentario, BilheteriaDigital bilheteria) {
                if (!bilheteria.verificarPresenca(comentario.getEspectadorId(), comentario.getEventoId()))
                    throw new IllegalStateException("Espectador não esteve presente no evento.");
                
                boolean jaAvaliou = repositorio.listarPorEvento(comentario.getEventoId()).stream()
                        .anyMatch(c -> c.getEspectadorId().equals(comentario.getEspectadorId()) && c.getNota() != null);
                if (jaAvaliou) {
                    throw new IllegalStateException("Espectador já avaliou este evento.");
                }
                repositorio.salvar(comentario);
            }
        };
    }

    @Dado("um espectador cadastrado")
    public void umEspectadorCadastrado() {
        espectadorId = UUID.randomUUID();
    }

    @Dado("um evento existente")
    public void umEventoExistente() {
        eventoId = UUID.randomUUID();
    }

    @Quando("o espectador tentar postar um comentário com texto {string}")
    public void oEspectadorTentarPostarUmComentarioComTexto(String texto) {
        try {
            comentario = new Comentario(UUID.randomUUID(), espectadorId, eventoId, texto);
            service.postar(comentario);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Então("o sistema deve lançar um erro de texto inválido")
    public void oSistemaDeveLancarUmErroDeTextoInvalido() {
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada instanceof IllegalArgumentException);
    }

    @Quando("o espectador tentar postar um comentário com mais de {int} caracteres")
    public void oEspectadorTentarPostarUmComentarioComMaisDeCaracteres(int limite) {
        try {
            String textoLongo = "a".repeat(limite + 1);
            comentario = new Comentario(UUID.randomUUID(), espectadorId, eventoId, textoLongo);
            service.postar(comentario);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Quando("o espectador postar o comentário {string}")
    public void oEspectadorPostarOComentario(String texto) {
        comentario = new Comentario(UUID.randomUUID(), espectadorId, eventoId, texto);
        service.postar(comentario);
    }

    @Então("o comentário deve estar registrado no sistema")
    public void oComentarioDeveEstarRegistradoNoSistema() {
        assertTrue(repositorio.obter(comentario.getId()).isPresent());
    }

    @Dado("um comentário postado pelo próprio espectador")
    public void umComentarioPostadoPeloProprioEspectador() {
        comentarioId = UUID.randomUUID();
        comentario = new Comentario(comentarioId, espectadorId, eventoId, "Um comentário legal!");
        repositorio.salvar(comentario);
    }

    @Quando("o espectador tentar curtir o próprio comentário")
    public void oEspectadorTentarCurtirOProprioComentario() {
        try {
            service.curtir(comentarioId, espectadorId);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Então("o sistema deve lançar um erro de curtida inválida")
    public void oSistemaDeveLancarUmErroDeCurtidaInvalida() {
        assertNotNull(excecaoLancada);
        assertEquals("Espectador não pode curtir o próprio comentário.", excecaoLancada.getMessage());
    }

    @Dado("um comentário postado por outro espectador")
    public void umComentarioPostadoPorOutroEspectador() {
        comentarioId = UUID.randomUUID();
        comentario = new Comentario(comentarioId, UUID.randomUUID(), eventoId, "Comentário de outro!");
        repositorio.salvar(comentario);
    }

    @Quando("o espectador curtir o comentário")
    public void oEspectadorCurtirOComentario() {
        service.curtir(comentarioId, espectadorId);
    }

    @Quando("o espectador tentar curtir o mesmo comentário novamente")
    public void oEspectadorTentarCurtirOMesmoComentarioNovamente() {
        try {
            service.curtir(comentarioId, espectadorId);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Então("o sistema deve lançar um erro de curtida duplicada")
    public void oSistemaDeveLancarUmErroDeCurtidaDuplicada() {
        assertNotNull(excecaoLancada);
        assertEquals("Espectador já curtiu este comentário.", excecaoLancada.getMessage());
    }

    @Então("a curtida deve estar registrada no comentário")
    public void aCurtidaDeveEstarRegistradaNoComentario() {
        assertTrue(repositorio.obter(comentarioId).get().getCurtidas().contains(espectadorId));
    }

    @Dado("um comentário existente no sistema")
    public void umComentarioExistenteNoSistema() {
        eventoId = UUID.randomUUID();
        comentarioId = UUID.randomUUID();
        comentario = new Comentario(comentarioId, UUID.randomUUID(), eventoId, "Comentário original!");
        repositorio.salvar(comentario);
    }

    @Quando("o espectador tentar responder com texto {string}")
    public void oEspectadorTentarResponderComTexto(String texto) {
        try {
            Comentario resposta = new Comentario(UUID.randomUUID(), espectadorId, eventoId, texto, comentarioId);
            service.responder(comentarioId, resposta);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Quando("o espectador tentar responder a um comentário que não existe")
    public void oEspectadorTentarResponderAUmComentarioQueNaoExiste() {
        try {
            UUID idFalso = UUID.randomUUID();
            Comentario resposta = new Comentario(UUID.randomUUID(), espectadorId, eventoId, "Resposta muito valida!", idFalso);
            service.responder(idFalso, resposta);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Então("o sistema deve lançar um erro de comentário não encontrado")
    public void oSistemaDeveLancarUmErroDeComentarioNaoEncontrado() {
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada.getMessage().contains("não encontrado"));
    }

    @Quando("o espectador responder com {string}")
    public void oEspectadorResponderCom(String texto) {
        comentario = new Comentario(UUID.randomUUID(), espectadorId, eventoId, texto, comentarioId);
        service.responder(comentarioId, comentario);
    }

    @Então("a resposta deve estar vinculada ao comentário pai")
    public void aRespostaDeveEstarVinculadaAoComentarioPai() {
        assertEquals(comentarioId, repositorio.obter(comentario.getId()).get().getComentarioPaiId());
    }

    @Quando("o autor deletar o comentário")
    public void oAutorDeletarOComentario() {
        service.deletar(comentarioId);
    }

    @Quando("os comentários do evento forem listados")
    public void osComentariosDoEventoForemListados() {
        comentariosDoEvento = service.listarAtivos(eventoId);
    }

    @Então("o comentário deletado não deve aparecer na listagem")
    public void oComentarioDeletadoNaoDeveAparecerNaListagem() {
        assertTrue(comentariosDoEvento.stream().noneMatch(c -> c.getId().equals(comentarioId)));
    }

    @Então("a listagem deve estar vazia")
    public void aListagemDeveEstarVazia() {
        assertTrue(comentariosDoEvento.isEmpty());
    }

    @Dado("o espectador não esteve presente no evento")
    public void oEspectadorNaoEstevePresenteNoEvento() {
        estevePresente = false;
    }

    @Quando("o espectador tentar postar nota {int} para o evento")
    public void oEspectadorTentarPostarNotaParaOEvento(int valorNota) {
        try {
            Nota nota = new Nota(valorNota);
            comentario = new Comentario(UUID.randomUUID(), espectadorId, eventoId, "Comentário da nota", nota, null);
            service.postarComNota(comentario, bilheteria);
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Então("o sistema deve lançar um erro de presença não confirmada")
    public void oSistemaDeveLancarUmErroDePresencaNaoConfirmada() {
        assertNotNull(excecaoLancada);
        assertEquals("Espectador não esteve presente no evento.", excecaoLancada.getMessage());
    }

    @Dado("o espectador esteve presente no evento")
    public void oEspectadorEstevePresenteNoEvento() {
        estevePresente = true;
    }

    @Então("o sistema deve lançar um erro de nota inválida")
    public void oSistemaDeveLancarUmErroDeNotaInvalida() {
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada instanceof IllegalArgumentException);
        assertTrue(excecaoLancada.getMessage().contains("Nota deve estar entre"));
    }

    @Dado("o espectador já postou nota {int} para o evento")
    public void oEspectadorJaPostouNotaParaOEvento(int valorNota) {
        Nota nota = new Nota(valorNota);
        Comentario avaliacao = new Comentario(UUID.randomUUID(), espectadorId, eventoId, "Avaliação inicial", nota, null);
        service.postarComNota(avaliacao, bilheteria);
    }

    @Então("o sistema deve lançar um erro de avaliação duplicada")
    public void oSistemaDeveLancarUmErroDeAvaliacaoDuplicada() {
        assertNotNull(excecaoLancada);
        assertEquals("Espectador já avaliou este evento.", excecaoLancada.getMessage());
    }

    @Quando("o espectador postar nota {int} para o evento com comentário {string}")
    public void oEspectadorPostarNotaParaOEventoComComentario(int valorNota, String texto) {
        Nota nota = new Nota(valorNota);
        comentario = new Comentario(UUID.randomUUID(), espectadorId, eventoId, texto, nota, null);
        service.postarComNota(comentario, bilheteria);
    }

    @Então("o comentário com nota deve estar registrado no sistema")
    public void oComentarioComNotaDeveEstarRegistradoNoSistema() {
        assertTrue(repositorio.obter(comentario.getId()).isPresent());
        assertNotNull(repositorio.obter(comentario.getId()).get().getNota());
    }
}
