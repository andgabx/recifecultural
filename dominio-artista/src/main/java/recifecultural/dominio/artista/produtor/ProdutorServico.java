package recifecultural.dominio.artista.produtor;

import recifecultural.dominio.artista.artista.IArtistaRepositorio;
import recifecultural.dominio.artista.artista.StatusArtista;

public class ProdutorServico {

    private final IProdutorRepositorio produtorRepositorio;
    private final IArtistaRepositorio artistaRepositorio;

    public ProdutorServico(IProdutorRepositorio produtorRepositorio,
                           IArtistaRepositorio artistaRepositorio) {
        if (produtorRepositorio == null) throw new IllegalArgumentException("Repositório de produtores é obrigatório.");
        if (artistaRepositorio == null) throw new IllegalArgumentException("Repositório de artistas é obrigatório.");
        this.produtorRepositorio = produtorRepositorio;
        this.artistaRepositorio = artistaRepositorio;
    }

    public ProdutorId cadastrar(String nomeFantasia, Cnpj cnpj, String email, String telefone) {
        if (produtorRepositorio.existePorCnpj(cnpj))
            throw new IllegalStateException("Já existe um produtor cadastrado com este CNPJ.");
        Produtor produtor = new Produtor(nomeFantasia, cnpj, email, telefone);
        produtorRepositorio.salvar(produtor);
        return produtor.getId();
    }

    public void atualizarContato(ProdutorId produtorId, String novoEmail, String novoTelefone) {
        Produtor produtor = obterOuLancar(produtorId);
        produtor.atualizarContato(novoEmail, novoTelefone);
        produtorRepositorio.atualizar(produtor);
    }

    public void suspender(ProdutorId produtorId) {
        Produtor produtor = obterOuLancar(produtorId);
        produtor.suspender();
        produtorRepositorio.atualizar(produtor);
    }

    public void reativar(ProdutorId produtorId) {
        Produtor produtor = obterOuLancar(produtorId);
        produtor.reativar();
        produtorRepositorio.atualizar(produtor);
    }

    public void inativar(ProdutorId produtorId) {
        Produtor produtor = obterOuLancar(produtorId);

        boolean possuiArtistasAtivos = artistaRepositorio
                .listarPorProdutor(produtorId).stream()
                .anyMatch(a -> a.getStatus() == StatusArtista.ATIVO);

        if (possuiArtistasAtivos)
            throw new IllegalStateException(
                    "Produtor possui artistas ativos vinculados. Inative-os antes de prosseguir.");

        produtor.inativar();
        produtor.anonimizarDadosPessoais();
        produtorRepositorio.atualizar(produtor);
    }

    private Produtor obterOuLancar(ProdutorId id) {
        return produtorRepositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado: " + id.valor()));
    }
}