package recifecultural.dominio.agenda.comentario;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ComentarioRepositorioComModeracaoTest {

    private ComentarioRepositorio delegado;
    private ComentarioRepositorioComModeracao decorator;

    @BeforeEach
    void setUp() {
        delegado = mock(ComentarioRepositorio.class);
        decorator = new ComentarioRepositorioComModeracao(delegado);
    }

    private Comentario comentarioAtivo(String texto) {
        return new Comentario(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                texto);
    }

    @Test
    void salvar_com_palavra_vetada_substitui_no_texto() {
        Comentario comentario = comentarioAtivo("Este é um texto com spam claramente visível");

        decorator.salvar(comentario);

        assertEquals("Este é um texto com **** claramente visível", comentario.getTexto());
    }

    @Test
    void salvar_texto_sem_palavras_vetadas_nao_e_alterado() {
        String textoLimpo = "Evento incrível com muita cultura e diversidade";
        Comentario comentario = comentarioAtivo(textoLimpo);

        decorator.salvar(comentario);

        assertEquals(textoLimpo, comentario.getTexto());
    }

    @Test
    void salvar_delega_para_repositorio_interno() {
        Comentario comentario = comentarioAtivo("Texto completamente limpo e sem problemas");

        decorator.salvar(comentario);

        verify(delegado).salvar(comentario);
    }

    @Test
    void atualizar_tambem_filtra_palavras_vetadas() {
        Comentario comentario = comentarioAtivo("Cuidado com fraude nesse tipo de ingresso");

        decorator.atualizar(comentario);

        assertEquals("Cuidado com ****** nesse tipo de ingresso", comentario.getTexto());
        verify(delegado).atualizar(comentario);
    }
}
