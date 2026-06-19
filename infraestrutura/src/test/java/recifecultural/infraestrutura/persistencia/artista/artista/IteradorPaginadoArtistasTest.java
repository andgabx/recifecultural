package recifecultural.infraestrutura.persistencia.artista.artista;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import recifecultural.dominio.artista.artista.Artista;
import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.artista.Iterador;
import recifecultural.dominio.artista.artista.StatusArtista;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IteradorPaginadoArtistasTest {

    private static final int TAMANHO_PAGINA = 50;

    @Mock
    private ArtistaJpaRepository jpa;

    private ArtistaMapeador mapeador;

    @BeforeEach
    void setUp() {
        mapeador = new ArtistaMapeador();
    }

    private ArtistaJpa criarArtistaJpa(String nome) {
        ArtistaJpa a = new ArtistaJpa();
        a.id = UUID.randomUUID();
        a.produtorId = UUID.randomUUID();
        a.nome = nome;
        a.status = StatusArtista.ATIVO;
        return a;
    }

    private Page<ArtistaJpa> paginaUnica(List<ArtistaJpa> itens) {
        return new PageImpl<>(itens, PageRequest.of(0, TAMANHO_PAGINA), itens.size());
    }

    private Page<ArtistaJpa> paginaParcial(List<ArtistaJpa> itens, int pagina, long totalElements) {
        return new PageImpl<>(itens, PageRequest.of(pagina, TAMANHO_PAGINA), totalElements);
    }

    @Test
    void dataset_vazio_temProximo_retorna_false() {
        Page<ArtistaJpa> paginaVazia = paginaUnica(List.of());
        when(jpa.findAll(any(Pageable.class))).thenReturn(paginaVazia);

        Iterador<Artista> iterador = new IteradorPaginadoArtistas(jpa, mapeador);

        assertFalse(iterador.temProximo());
    }

    @Test
    void dataset_com_um_artista_itera_corretamente() {
        ArtistaJpa artistaJpa = criarArtistaJpa("Alceu Valença");
        Page<ArtistaJpa> pagina = paginaUnica(List.of(artistaJpa));
        when(jpa.findAll(any(Pageable.class))).thenReturn(pagina);

        Iterador<Artista> iterador = new IteradorPaginadoArtistas(jpa, mapeador);

        assertTrue(iterador.temProximo());
        Artista artista = iterador.proximo();
        assertEquals("Alceu Valença", artista.getNome());
        assertFalse(iterador.temProximo());
    }

    @Test
    void dataset_com_varios_artistas_itera_todos() {
        List<ArtistaJpa> artistasJpa = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            artistasJpa.add(criarArtistaJpa("Artista " + i));
        }
        Page<ArtistaJpa> pagina = paginaUnica(artistasJpa);
        when(jpa.findAll(any(Pageable.class))).thenReturn(pagina);

        Iterador<Artista> iterador = new IteradorPaginadoArtistas(jpa, mapeador);

        List<Artista> coletados = new ArrayList<>();
        while (iterador.temProximo()) {
            coletados.add(iterador.proximo());
        }

        assertEquals(5, coletados.size());
        for (int i = 0; i < 5; i++) {
            assertEquals("Artista " + (i + 1), coletados.get(i).getNome());
        }
    }

    @Test
    void paginacao_atravessa_multiplas_paginas() {
        List<ArtistaJpa> primeiraPaginaItens = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            primeiraPaginaItens.add(criarArtistaJpa("Artista " + i));
        }
        List<ArtistaJpa> segundaPaginaItens = new ArrayList<>();
        for (int i = 51; i <= 55; i++) {
            segundaPaginaItens.add(criarArtistaJpa("Artista " + i));
        }

        Page<ArtistaJpa> primeiraPagina = paginaParcial(primeiraPaginaItens, 0, 55);
        Page<ArtistaJpa> segundaPagina = paginaParcial(segundaPaginaItens, 1, 55);

        when(jpa.findAll(PageRequest.of(0, TAMANHO_PAGINA))).thenReturn(primeiraPagina);
        when(jpa.findAll(PageRequest.of(1, TAMANHO_PAGINA))).thenReturn(segundaPagina);

        Iterador<Artista> iterador = new IteradorPaginadoArtistas(jpa, mapeador);

        List<Artista> coletados = new ArrayList<>();
        while (iterador.temProximo()) {
            coletados.add(iterador.proximo());
        }

        assertEquals(55, coletados.size());
    }

    @Test
    void proximo_apos_exaustao_lanca_NoSuchElementException() {
        Page<ArtistaJpa> paginaVazia = paginaUnica(List.of());
        when(jpa.findAll(any(Pageable.class))).thenReturn(paginaVazia);

        Iterador<Artista> iterador = new IteradorPaginadoArtistas(jpa, mapeador);

        assertThrows(NoSuchElementException.class, iterador::proximo);
    }

    @Test
    void repositorio_iterarTodos_retorna_iterador_funcional() {
        ArtistaJpa artistaJpa = criarArtistaJpa("Nação Zumbi");
        Page<ArtistaJpa> pagina = paginaUnica(List.of(artistaJpa));
        when(jpa.findAll(any(Pageable.class))).thenReturn(pagina);

        ArtistaRepositorioImpl repositorio = new ArtistaRepositorioImpl(jpa, mapeador);
        Iterador<Artista> iterador = repositorio.iterarTodos();

        assertTrue(iterador.temProximo());
        Artista artista = iterador.proximo();
        assertEquals("Nação Zumbi", artista.getNome());
        assertFalse(iterador.temProximo());
    }

    @Test
    void dois_iteradores_independentes_nao_interferem() {
        List<ArtistaJpa> artistasJpa = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            artistasJpa.add(criarArtistaJpa("Artista " + i));
        }
        Page<ArtistaJpa> pagina = paginaUnica(artistasJpa);
        when(jpa.findAll(any(Pageable.class))).thenReturn(pagina);

        Iterador<Artista> iteradorA = new IteradorPaginadoArtistas(jpa, mapeador);
        Iterador<Artista> iteradorB = new IteradorPaginadoArtistas(jpa, mapeador);

        assertTrue(iteradorA.temProximo());
        assertEquals("Artista 1", iteradorA.proximo().getNome());

        assertTrue(iteradorB.temProximo());
        assertEquals("Artista 1", iteradorB.proximo().getNome());

        assertEquals("Artista 2", iteradorA.proximo().getNome());
        assertEquals("Artista 2", iteradorB.proximo().getNome());

        assertEquals("Artista 3", iteradorA.proximo().getNome());
        assertEquals("Artista 3", iteradorB.proximo().getNome());

        assertFalse(iteradorA.temProximo());
        assertFalse(iteradorB.temProximo());
    }

    @Test
    void iteracao_completa_retorna_exatamente_n_itens_sem_duplicatas() {
        List<ArtistaJpa> artistasJpa = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            artistasJpa.add(criarArtistaJpa("Artista " + i));
        }
        Page<ArtistaJpa> pagina = paginaUnica(artistasJpa);
        when(jpa.findAll(any(Pageable.class))).thenReturn(pagina);

        Iterador<Artista> iterador = new IteradorPaginadoArtistas(jpa, mapeador);

        Set<String> nomes = new HashSet<>();
        while (iterador.temProximo()) {
            nomes.add(iterador.proximo().getNome());
        }

        assertEquals(30, nomes.size());
    }
}
