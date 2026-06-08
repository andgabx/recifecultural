package recifecultural.dominio.artista.artista;

import java.util.List;
import java.util.NoSuchElementException;

public class IteradorDeArtistas implements Iterador<Artista> {

    private final List<Artista> artistas;
    private int posicaoAtual;

    public IteradorDeArtistas(List<Artista> artistas) {
        if (artistas == null) throw new IllegalArgumentException("A lista de artistas não pode ser nula.");
        this.artistas = artistas;
        this.posicaoAtual = 0;
    }

    @Override
    public boolean temProximo() {
        return posicaoAtual < artistas.size();
    }

    @Override
    public Artista proximo() {
        if (!temProximo()) {
            throw new NoSuchElementException("Não há mais artistas para iterar.");
        }
        return artistas.get(posicaoAtual++);
    }
}
