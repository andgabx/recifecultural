package recifecultural.infraestrutura.persistencia.artista.artista;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import recifecultural.dominio.artista.artista.Artista;
import recifecultural.dominio.artista.artista.Iterador;

import java.util.List;
import java.util.NoSuchElementException;

/*
 * ConcreteIterator (Par 5) — percorre todos os artistas do banco em páginas
 * via JPA. Cada esgotamento do buffer dispara a busca da próxima página;
 * o conjunto inteiro nunca é materializado em memória.
 */
class IteradorPaginadoArtistas implements Iterador<Artista> {

    private static final int TAMANHO_PAGINA = 50;

    private final ArtistaJpaRepository jpa;
    private final ArtistaMapeador mapeador;

    private List<Artista> buffer = List.of();
    private int cursor = 0;
    private int proximaPagina = 0;
    private boolean ultimaPaginaLida = false;

    IteradorPaginadoArtistas(ArtistaJpaRepository jpa, ArtistaMapeador mapeador) {
        if (jpa == null) throw new IllegalArgumentException("Repositório JPA é obrigatório.");
        if (mapeador == null) throw new IllegalArgumentException("Mapeador é obrigatório.");
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public boolean temProximo() {
        if (cursor < buffer.size()) return true;
        if (ultimaPaginaLida) return false;
        carregarProximaPagina();
        return cursor < buffer.size();
    }

    @Override
    public Artista proximo() {
        if (!temProximo()) {
            throw new NoSuchElementException("Não há mais artistas para iterar.");
        }
        return buffer.get(cursor++);
    }

    private void carregarProximaPagina() {
        Page<ArtistaJpa> pagina = jpa.findAll(PageRequest.of(proximaPagina, TAMANHO_PAGINA));
        buffer = pagina.getContent().stream().map(mapeador::toDomain).toList();
        cursor = 0;
        proximaPagina++;
        if (pagina.isLast() || buffer.isEmpty()) {
            ultimaPaginaLida = true;
        }
    }
}
