package recifecultural.dominio.artistas;

import java.util.List;
import java.util.Optional;

public interface IArtistaRepositorio {

    void salvar(Artista artista);

    Optional<Artista> buscarPorId(ArtistId id);

    Optional<Artista> buscarPorNome(String nome);

    List<Artista> buscarPorStatus(StatusArtista status);
}
