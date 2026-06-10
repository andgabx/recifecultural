package recifecultural.dominio.artista.artista;

import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.dominio.artista.produtor.IProdutorRepositorio;
import recifecultural.dominio.artista.produtor.StatusProdutor;

public class ArtistaServico {

    private final IArtistaRepositorio artistaRepositorio;
    private final IProdutorRepositorio produtorRepositorio;

    public ArtistaServico(IArtistaRepositorio artistaRepositorio,
                          IProdutorRepositorio produtorRepositorio) {
        if (artistaRepositorio == null) throw new IllegalArgumentException("Repositório de artistas é obrigatório.");
        if (produtorRepositorio == null) throw new IllegalArgumentException("Repositório de produtores é obrigatório.");
        this.artistaRepositorio = artistaRepositorio;
        this.produtorRepositorio = produtorRepositorio;
    }

    public ArtistaId cadastrar(ProdutorId produtorId, String nome, RiderTecnico riderTecnico) {
        produtorRepositorio.obterPorId(produtorId)
                .filter(p -> p.getStatus() == StatusProdutor.ATIVO)
                .orElseThrow(() -> new IllegalStateException("Produtor não encontrado ou inativo."));

        if (artistaRepositorio.existePorNomeEProdutor(nome, produtorId))
            throw new IllegalStateException("Já existe um artista com este nome vinculado ao produtor.");

        Artista artista = new Artista(produtorId, nome, riderTecnico);
        artistaRepositorio.salvar(artista);
        return artista.getId();
    }

    public void atualizarRider(ArtistaId artistaId, RiderTecnico novoRider) {
        Artista artista = artistaRepositorio.obterPorId(artistaId)
                .orElseThrow(() -> new IllegalArgumentException("Artista não encontrado."));
        artista.atualizarRider(novoRider);
        artistaRepositorio.atualizar(artista);
    }

    public void inativar(ArtistaId artistaId) {
        Artista artista = artistaRepositorio.obterPorId(artistaId)
                .orElseThrow(() -> new IllegalArgumentException("Artista não encontrado."));

        artista.inativar();
        artistaRepositorio.atualizar(artista);
    }

    /**
     * Cria um iterador sobre os itens do rider técnico de um artista,
     * delegando ao repositório (Aggregate no padrão Iterator GoF).
     *
     * @param artistaId o ID do artista cujos itens do rider serão iterados
     * @return um {@link Iterador} de {@link ItemRider}
     */
    public Iterador<ItemRider> iterarItensRider(ArtistaId artistaId) {
        return artistaRepositorio.criarIteradorDeItensRider(artistaId);
    }
}
