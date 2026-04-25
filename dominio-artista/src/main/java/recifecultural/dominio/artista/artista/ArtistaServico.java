package recifecultural.dominio.artista.artista;

import recifecultural.dominio.artista.produtor.IEventoConsultaPort;
import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.dominio.artista.produtor.IProdutorRepositorio;
import recifecultural.dominio.artista.produtor.StatusProdutor;

public class ArtistaServico {

    private final IArtistaRepositorio artistaRepositorio;
    private final IProdutorRepositorio produtorRepositorio;
    private final IEventoConsultaPort eventoConsultaPort;

    public ArtistaServico(IArtistaRepositorio artistaRepositorio,
                          IProdutorRepositorio produtorRepositorio,
                          IEventoConsultaPort eventoConsultaPort) {
        if (artistaRepositorio == null) throw new IllegalArgumentException("Repositório de artistas é obrigatório.");
        if (produtorRepositorio == null) throw new IllegalArgumentException("Repositório de produtores é obrigatório.");
        if (eventoConsultaPort == null) throw new IllegalArgumentException("Porta de consulta de eventos é obrigatória.");
        this.artistaRepositorio = artistaRepositorio;
        this.produtorRepositorio = produtorRepositorio;
        this.eventoConsultaPort = eventoConsultaPort;
    }

    public ArtistaId cadastrar(ProdutorId produtorId, String nome,
                               GeneroMusical generoMusical, RiderTecnico riderTecnico) {
        produtorRepositorio.obterPorId(produtorId)
                .filter(p -> p.getStatus() == StatusProdutor.ATIVO)
                .orElseThrow(() -> new IllegalStateException("Produtor não encontrado ou inativo."));

        if (artistaRepositorio.existePorNomeEProdutor(nome, produtorId))
            throw new IllegalStateException("Já existe um artista com este nome vinculado ao produtor.");

        Artista artista = new Artista(produtorId, nome, generoMusical, riderTecnico);
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

        // Anti-corruption layer: consulta a agenda sem depender diretamente dela
        if (eventoConsultaPort.possuiEventosFuturosConfirmados(artistaId.valor()))
            throw new IllegalStateException("Artista possui eventos futuros confirmados. Realize o cancelamento antes de inativar.");

        artista.inativar();
        artistaRepositorio.atualizar(artista);
    }
}