package recifecultural.dominio.artista.produtor;

import recifecultural.dominio.artista.artista.IArtistaRepositorio;
import recifecultural.dominio.artista.artista.StatusArtista;

public class ProdutorServico {

    private final IProdutorRepositorio produtorRepositorio;
    private final IArtistaRepositorio artistaRepositorio;
    private final IHistoricoStatusProdutorRepositorio historicoRepositorio;

    public ProdutorServico(IProdutorRepositorio produtorRepositorio,
                           IArtistaRepositorio artistaRepositorio) {
        this(produtorRepositorio, artistaRepositorio, null);
    }

    public ProdutorServico(IProdutorRepositorio produtorRepositorio,
                           IArtistaRepositorio artistaRepositorio,
                           IHistoricoStatusProdutorRepositorio historicoRepositorio) {
        if (produtorRepositorio == null) throw new IllegalArgumentException("Repositório de produtores é obrigatório.");
        if (artistaRepositorio == null) throw new IllegalArgumentException("Repositório de artistas é obrigatório.");
        this.produtorRepositorio = produtorRepositorio;
        this.artistaRepositorio = artistaRepositorio;
        this.historicoRepositorio = historicoRepositorio;
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

    public void suspender(ProdutorId produtorId, String responsavel, String motivo) {
        Produtor produtor = obterOuLancar(produtorId);
        StatusProdutor anterior = produtor.getStatus();
        produtor.suspender();
        produtorRepositorio.atualizar(produtor);
        registrarHistorico(produtorId, anterior, StatusProdutor.SUSPENSO, responsavel, motivo);
    }

    public void suspender(ProdutorId produtorId) {
        suspender(produtorId, "SISTEMA", null);
    }

    public void reativar(ProdutorId produtorId, String responsavel, String motivo) {
        Produtor produtor = obterOuLancar(produtorId);
        StatusProdutor anterior = produtor.getStatus();
        produtor.reativar();
        produtorRepositorio.atualizar(produtor);
        registrarHistorico(produtorId, anterior, StatusProdutor.ATIVO, responsavel, motivo);
    }

    public void reativar(ProdutorId produtorId) {
        reativar(produtorId, "SISTEMA", null);
    }

    public void inativar(ProdutorId produtorId, String responsavel, String motivo) {
        Produtor produtor = obterOuLancar(produtorId);

        boolean possuiArtistasAtivos = artistaRepositorio
                .listarPorProdutor(produtorId).stream()
                .anyMatch(a -> a.getStatus() == StatusArtista.ATIVO);

        if (possuiArtistasAtivos)
            throw new IllegalStateException(
                    "Produtor possui artistas ativos vinculados. Inative-os antes de prosseguir.");

        StatusProdutor anterior = produtor.getStatus();
        produtor.inativar();
        produtor.anonimizarDadosPessoais();
        produtorRepositorio.atualizar(produtor);
        registrarHistorico(produtorId, anterior, StatusProdutor.INATIVO, responsavel, motivo);
    }

    public void inativar(ProdutorId produtorId) {
        inativar(produtorId, "SISTEMA", null);
    }

    private void registrarHistorico(ProdutorId produtorId,
                                    StatusProdutor anterior,
                                    StatusProdutor novo,
                                    String responsavel,
                                    String motivo) {
        if (historicoRepositorio == null) return;
        historicoRepositorio.salvar(new HistoricoStatusProdutor(produtorId, anterior, novo, responsavel, motivo));
    }

    private Produtor obterOuLancar(ProdutorId id) {
        return produtorRepositorio.obterPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado: " + id.valor()));
    }
}