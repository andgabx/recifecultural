package recifecultural.dominio.artistas;

import java.util.UUID;

public class ArtistaServico {

    private final IArtistaRepositorio repositorio;

    public ArtistaServico(IArtistaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public Artista cadastrar(String nome, String bio, String email, String telefone) {
        repositorio.buscarPorNome(nome).ifPresent(existente -> {
            throw new IllegalStateException("Já existe um artista cadastrado com o nome: " + nome);
        });

        Artista artista = new Artista(nome, bio, email, telefone);
        repositorio.salvar(artista);
        return artista;
    }

    public void aprovar(ArtistId id, UUID promotorId) {
        Artista artista = buscarOuLancarExcecao(id);
        artista.aprovar(promotorId);
        repositorio.salvar(artista);
    }

    public void rejeitar(ArtistId id, UUID promotorId, String motivo) {
        Artista artista = buscarOuLancarExcecao(id);
        artista.rejeitar(promotorId, motivo);
        repositorio.salvar(artista);
    }

    public void resubmeter(ArtistId id) {
        Artista artista = buscarOuLancarExcecao(id);
        artista.resubmeter();
        repositorio.salvar(artista);
    }

    private Artista buscarOuLancarExcecao(ArtistId id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Artista não encontrado com id: " + id));
    }
}
