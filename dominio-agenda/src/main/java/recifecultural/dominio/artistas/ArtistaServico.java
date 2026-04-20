package recifecultural.dominio.artistas;

import static org.apache.commons.lang3.Validate.notNull;

import java.util.UUID;

public class ArtistaServico {

    private final IArtistaRepositorio repositorio;

    public ArtistaServico(IArtistaRepositorio repositorio) {
        notNull(repositorio, "O repositório de artista não pode ser nulo.");
        this.repositorio = repositorio;
    }

    public Artista cadastrar(String nome, String bio, String email, String telefone) {
        Artista existente = repositorio.buscarPorNome(nome);
        if (existente != null) {
            throw new IllegalStateException("Já existe um artista cadastrado com o nome: " + nome);
        }

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
        Artista artista = repositorio.buscarPorId(id);
        notNull(artista, "Artista não encontrado com id: " + id);
        return artista;
    }
}
