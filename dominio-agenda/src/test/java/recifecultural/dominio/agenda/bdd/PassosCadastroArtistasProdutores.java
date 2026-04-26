package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import recifecultural.dominio.artista.artista.*;
import recifecultural.dominio.artista.produtor.*;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.MockitoAnnotations;

public class PassosCadastroArtistasProdutores {

    private IArtistaRepositorio artistaRepo;
    private IProdutorRepositorio produtorRepo;
    private ArtistaServico artistaServico;
    private ProdutorServico produtorServico;

    private ProdutorId produtorIdAtual;
    private ArtistaId artistaIdAtual;

    public PassosCadastroArtistasProdutores() {
        MockitoAnnotations.openMocks(this);
        artistaRepo     = mock(IArtistaRepositorio.class);
        produtorRepo    = mock(IProdutorRepositorio.class);
        artistaServico  = new ArtistaServico(artistaRepo, produtorRepo);
        produtorServico = new ProdutorServico(produtorRepo, artistaRepo);
    }

    @Dado("que não existe produtor cadastrado com o CNPJ {string}")
    public void queNaoExisteProdutorCadastradoComOCnpj(String cnpj) {
        when(produtorRepo.existePorCnpj(new Cnpj(cnpj))).thenReturn(false);
    }

    @Quando("eu cadastrar um produtor com o CNPJ {string} e nome {string}")
    public void euCadastrarUmProdutorComOCnpjENome(String cnpj, String nome) {
        produtorIdAtual = produtorServico.cadastrar(
                nome, new Cnpj(cnpj), "contato@produtor.com", "81999999999");
    }

    @Então("o produtor deve ser salvo com sucesso")
    public void oProdutorDeveSerSalvoComSucesso() {
        assertNotNull(produtorIdAtual);
        verify(produtorRepo, times(1)).salvar(any(Produtor.class));
    }

    @Dado("que o produtor {string} possui status {string}")
    public void queOProdutorPossuiStatus(String nomeProdutor, String status) {
        produtorIdAtual = ProdutorId.novo();
        Produtor produtor = new Produtor(
                produtorIdAtual, nomeProdutor,
                new Cnpj("11222333000181"),
                "contato@produtor.com", "81999999999",
                StatusProdutor.valueOf(status));
        when(produtorRepo.obterPorId(produtorIdAtual)).thenReturn(Optional.of(produtor));
    }

    @E("não existe um artista chamado {string} vinculado à {string}")
    public void naoExisteUmArtistaChamadoVinculadoA(String nomeArtista, String nomeProdutor) {
        when(artistaRepo.existePorNomeEProdutor(nomeArtista, produtorIdAtual)).thenReturn(false);
    }

    @Quando("eu cadastrar o artista {string} para o produtor {string}")
    public void euCadastrarOArtistaParaOProdutor(String nomeArtista, String nomeProdutor) {
        artistaIdAtual = artistaServico.cadastrar(produtorIdAtual, nomeArtista, RiderTecnico.vazio());
    }

    @Então("o artista deve ser salvo com sucesso")
    public void oArtistaDeveSerSalvoComSucesso() {
        assertNotNull(artistaIdAtual);
        verify(artistaRepo, times(1)).salvar(any(Artista.class));
    }

    @Dado("que existe um produtor {string} com status {string}")
    public void queExisteUmProdutorComStatus(String nomeProdutor, String status) {
        produtorIdAtual = ProdutorId.novo();
        Produtor produtor = new Produtor(
                produtorIdAtual, nomeProdutor,
                new Cnpj("11222333000181"),
                "contato@produtor.com", "81999999999",
                StatusProdutor.valueOf(status));
        when(produtorRepo.obterPorId(produtorIdAtual)).thenReturn(Optional.of(produtor));
    }

    @E("o produtor não possui artistas com status {string} vinculados")
    public void oProdutorNaoPossuiArtistasComStatusVinculados(String status) {
        when(artistaRepo.listarPorProdutor(produtorIdAtual)).thenReturn(Collections.emptyList());
    }

    @Quando("eu solicitar a inativação do produtor {string}")
    public void euSolicitarAInativacaoDoProdutor(String nomeProdutor) {
        produtorServico.inativar(produtorIdAtual);
    }

    @Então("o status do produtor deve mudar para {string}")
    public void oStatusDoProdutorDeveMudarPara(String status) {
        verify(produtorRepo, times(1)).atualizar(
                argThat(p -> p.getStatus() == StatusProdutor.valueOf(status)));
    }

    @E("os dados pessoais de e-mail e telefone devem ser anonimizados")
    public void osDadosPessoaisDeEmailETelefoneDevemSerAnonimizados() {
        verify(produtorRepo, times(1)).atualizar(argThat(p ->
                "anonimizado@lgpd.recife.br".equals(p.getEmail()) && p.getTelefone() == null));
    }


    @Dado("que o artista {string} possui status {string}")
    public void queOArtistaPossuiStatus(String nomeArtista, String status) {
        artistaIdAtual = ArtistaId.novo();
        Artista artista = new Artista(
                artistaIdAtual, ProdutorId.novo(),
                nomeArtista, RiderTecnico.vazio(),
                StatusArtista.valueOf(status));
        when(artistaRepo.obterPorId(artistaIdAtual)).thenReturn(Optional.of(artista));
    }

    @Quando("eu solicitar a inativação do artista {string}")
    public void euSolicitarAInativacaoDoArtista(String nomeArtista) {
        artistaServico.inativar(artistaIdAtual);
    }

    @Então("o status do artista deve mudar para {string}")
    public void oStatusDoArtistaDeveMudarPara(String status) {
        verify(artistaRepo, times(1)).atualizar(
                argThat(a -> a.getStatus() == StatusArtista.valueOf(status)));
    }


    @Quando("eu atualizar o rider técnico do artista com os itens {string} e {string}")
    public void euAtualizarORiderTecnicoDoArtistaComOsItensE(String item1, String item2) {
        RiderTecnico novoRider = new RiderTecnico(
                Set.of(ItemRider.valueOf(item1), ItemRider.valueOf(item2)));
        artistaServico.atualizarRider(artistaIdAtual, novoRider);
    }

    @Então("os itens do rider técnico devem ser atualizados com sucesso")
    public void osItensDoRiderTecnicoDevemSerAtualizadosComSucesso() {
        verify(artistaRepo, times(1)).atualizar(
                argThat(a -> !a.getRiderTecnico().itens().isEmpty()));
    }
}