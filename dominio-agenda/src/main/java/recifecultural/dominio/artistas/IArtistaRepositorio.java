package recifecultural.dominio.artistas;

import java.util.List;

public interface IArtistaRepositorio {

    void salvar(Artista artista);

    Artista buscarPorId(ArtistId id);

    Artista buscarPorNome(String nome);

    List<Artista> buscarPorStatus(StatusArtista status);
}
