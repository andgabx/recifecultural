package recifecultural.dominio.artistas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArtistaRepositorioEmMemoria implements IArtistaRepositorio {

    private final Map<ArtistId, Artista> artistas = new HashMap<>();

    @Override
    public void salvar(Artista artista) {
        artistas.put(artista.getId(), artista);
    }

    @Override
    public Artista buscarPorId(ArtistId id) {
        return artistas.get(id);
    }

    @Override
    public Artista buscarPorNome(String nome) {
        return artistas.values().stream()
                .filter(artista -> artista.getNome().equals(nome))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Artista> buscarPorStatus(StatusArtista status) {
        return artistas.values().stream()
                .filter(artista -> artista.getStatus() == status)
                .toList();
    }
}
