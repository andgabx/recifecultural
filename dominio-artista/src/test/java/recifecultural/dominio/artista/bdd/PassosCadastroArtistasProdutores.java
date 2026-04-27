package recifecultural.dominio.artista.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;

import recifecultural.dominio.artista.artista.Artista;
import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.artista.ArtistaServico;
import recifecultural.dominio.artista.artista.IArtistaRepositorio;
import recifecultural.dominio.artista.artista.ItemRider;
import recifecultural.dominio.artista.artista.RiderTecnico;
import recifecultural.dominio.artista.artista.StatusArtista;
import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.IProdutorRepositorio;
import recifecultural.dominio.artista.produtor.Produtor;
import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.dominio.artista.produtor.ProdutorServico;
import recifecultural.dominio.artista.produtor.StatusProdutor;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PassosCadastroArtistasProdutores {

    private IArtistaRepositorio artistaRepo;
    private IProdutorRepositorio produtorRepo;
    private ArtistaServico artistaServico;
    private ProdutorServico produtorServico;

    private ProdutorId produtorIdAtual;
    private Produtor produtorSalvo;
    private ArtistaId artistaIdAtual;
    private Artista artistaSalvo;
    private Exception excecaoCapturada;

    public PassosCadastroArtistasProdutores() {
        MockitoAnnotations.openMocks(this);
        artistaRepo     = mock(IArtistaRepositorio.class);
        produtorRepo    = mock(IProdutorRepositorio.class);
        artistaServico  = new ArtistaServico(artistaRepo, produtorRepo);
        produtorServico = new ProdutorServico(produtorRepo, artistaRepo);
    }

    // =========================================================================
    // DADO — Produtor
    // =========================================================================

    @Dado("que existe um produtor salvo no repositório com status {string}")
    public void queExisteUmProdutorSalvoNoRepositorioComStatus(String status) {
        produtorIdAtual = ProdutorId.novo();
        Produtor p = new Produtor(
                produtorIdAtual, "Produtor Teste",
                new Cnpj("11222333000181"),
                "contato@produtor.com", "81999999999",
                StatusProdutor.valueOf(status));
        when(produtorRepo.obterPorId(produtorIdAtual)).thenReturn(Optional.of(p));
    }

    @Dado("que não existe produtor cadastrado com o CNPJ {string}")
    public void queNaoExisteProdutorCadastradoComOCnpj(String cnpj) {
        try {
            when(produtorRepo.existePorCnpj(new Cnpj(cnpj))).thenReturn(false);
        } catch (IllegalArgumentException ignored) {
            // CNPJ inválido: repositório nunca será consultado
        }
    }

    @Dado("que existe um produtor cadastrado com o CNPJ {string}")
    public void queExisteUmProdutorCadastradoComOCnpj(String cnpj) {
        when(produtorRepo.existePorCnpj(new Cnpj(cnpj))).thenReturn(true);
    }

    // =========================================================================
    // DADO — Artista
    // =========================================================================

    @Dado("que existe um artista salvo no repositório com status {string}")
    public void queExisteUmArtistaSalvoNoRepositorioComStatus(String status) {
        if (produtorIdAtual == null) produtorIdAtual = ProdutorId.novo();
        artistaIdAtual = ArtistaId.novo();
        Artista a = new Artista(
                artistaIdAtual, produtorIdAtual,
                "Artista Teste", RiderTecnico.vazio(),
                StatusArtista.valueOf(status));
        when(artistaRepo.obterPorId(artistaIdAtual)).thenReturn(Optional.of(a));
    }

    @Dado("existe um artista salvo com o nome {string} para este produtor")
    public void existeUmArtistaSalvoComONomeParaEsteProdutor(String nome) {
        when(artistaRepo.existePorNomeEProdutor(nome, produtorIdAtual)).thenReturn(true);
    }

    @Dado("não existe artista com o nome {string} para este produtor")
    public void naoExisteArtistaComONomeParaEsteProdutor(String nome) {
        when(artistaRepo.existePorNomeEProdutor(nome, produtorIdAtual)).thenReturn(false);
    }

    // =========================================================================
    // DADO — Artistas vinculados ao produtor
    // =========================================================================

    @Dado("o produtor possui ao menos um artista com status {string}")
    public void oProdutorPossuiAoMenosUmArtistaComStatus(String status) {
        Artista a = new Artista(
                ArtistaId.novo(), produtorIdAtual,
                "Artista Vinculado", RiderTecnico.vazio(),
                StatusArtista.valueOf(status));
        when(artistaRepo.listarPorProdutor(produtorIdAtual)).thenReturn(List.of(a));
    }

    @Dado("o produtor não possui artistas ativos vinculados")
    public void oProdutorNaoPossuiArtistasAtivosVinculados() {
        when(artistaRepo.listarPorProdutor(produtorIdAtual)).thenReturn(Collections.emptyList());
    }

    // =========================================================================
    // QUANDO — Cadastro de Produtor
    // =========================================================================

    @Quando("eu solicitar o cadastro de um produtor com nome fantasia {string}, CNPJ {string}, e-mail {string} e telefone {string}")
    public void euSolicitarOCadastroDeProdutorCompleto(String nome, String cnpj, String email, String telefone) {
        excecaoCapturada = null;
        produtorSalvo = null;
        try {
            produtorIdAtual = produtorServico.cadastrar(nome, new Cnpj(cnpj), email, telefone);
            ArgumentCaptor<Produtor> cap = ArgumentCaptor.forClass(Produtor.class);
            verify(produtorRepo, atLeastOnce()).salvar(cap.capture());
            produtorSalvo = cap.getValue();
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    // =========================================================================
    // QUANDO — Cadastro de Artista (gênero musical ignorado — campo removido)
    // =========================================================================

    @Quando("eu solicitar o cadastro de um artista com nome {string}, gênero musical {string} e este produtor")
    public void euSolicitarOCadastroDeArtista(String nome, String generoMusical) {
        excecaoCapturada = null;
        artistaSalvo = null;
        try {
            artistaIdAtual = artistaServico.cadastrar(produtorIdAtual, nome, RiderTecnico.vazio());
            ArgumentCaptor<Artista> cap = ArgumentCaptor.forClass(Artista.class);
            verify(artistaRepo, atLeastOnce()).salvar(cap.capture());
            artistaSalvo = cap.getValue();
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    // =========================================================================
    // QUANDO — Ciclo de vida do Produtor
    // =========================================================================

    @Quando("eu solicitar a inativação deste produtor")
    public void euSolicitarAInativacaoDesteProdutor() {
        excecaoCapturada = null;
        try { produtorServico.inativar(produtorIdAtual); }
        catch (Exception e) { excecaoCapturada = e; }
    }

    @Quando("eu solicitar a suspensão deste produtor")
    public void euSolicitarASuspensaoDesteProdutor() {
        excecaoCapturada = null;
        try { produtorServico.suspender(produtorIdAtual); }
        catch (Exception e) { excecaoCapturada = e; }
    }

    @Quando("eu solicitar a reativação deste produtor")
    public void euSolicitarAReativacaoDesteProdutor() {
        excecaoCapturada = null;
        try { produtorServico.reativar(produtorIdAtual); }
        catch (Exception e) { excecaoCapturada = e; }
    }

    // =========================================================================
    // QUANDO — Ciclo de vida do Artista
    // =========================================================================

    @Quando("eu solicitar a inativação deste artista")
    public void euSolicitarAInativacaoDesteArtista() {
        excecaoCapturada = null;
        try { artistaServico.inativar(artistaIdAtual); }
        catch (Exception e) { excecaoCapturada = e; }
    }

    @Quando("eu solicitar a atualização do rider técnico deste artista com os itens {string}")
    public void euSolicitarAtualizacaoDoRiderUmItem(String item1) {
        atualizarRider(item1);
    }

    @Quando("eu solicitar a atualização do rider técnico deste artista com os itens {string}, {string} e {string}")
    public void euSolicitarAtualizacaoDoRiderTresItens(String item1, String item2, String item3) {
        atualizarRider(item1, item2, item3);
    }

    private void atualizarRider(String... itens) {
        excecaoCapturada = null;
        try {
            Set<ItemRider> set = EnumSet.noneOf(ItemRider.class);
            for (String i : itens) set.add(ItemRider.valueOf(i.trim()));
            artistaServico.atualizarRider(artistaIdAtual, new RiderTecnico(set));
        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    // =========================================================================
    // ENTÃO — Sucesso no cadastro de Produtor
    // =========================================================================

    @Então("o produtor deve ser cadastrado com sucesso")
    public void oProdutorDeveSerCadastradoComSucesso() {
        assertNull(excecaoCapturada, mensagemFalha());
        assertNotNull(produtorIdAtual);
        verify(produtorRepo, times(1)).salvar(any(Produtor.class));
    }

    @Então("o status do produtor deve ser {string}")
    public void oStatusDoProdutorDeveSer(String status) {
        assertNotNull(produtorSalvo);
        assertEquals(StatusProdutor.valueOf(status), produtorSalvo.getStatus());
    }

    // =========================================================================
    // ENTÃO — Sucesso no cadastro de Artista
    // =========================================================================

    @Então("o artista deve ser cadastrado com sucesso")
    public void oArtistaDeveSerCadastradoComSucesso() {
        assertNull(excecaoCapturada, mensagemFalha());
        assertNotNull(artistaIdAtual);
        verify(artistaRepo, times(1)).salvar(any(Artista.class));
    }

    @Então("o status do artista deve ser {string}")
    public void oStatusDoArtistaDeveSer(String status) {
        assertNotNull(artistaSalvo);
        assertEquals(StatusArtista.valueOf(status), artistaSalvo.getStatus());
    }

    // =========================================================================
    // ENTÃO — Atualização de status via repositório
    // =========================================================================

    @Então("o produtor deve ser atualizado para o status {string}")
    public void oProdutorDeveSerAtualizadoParaOStatus(String status) {
        assertNull(excecaoCapturada, mensagemFalha());
        verify(produtorRepo, times(1)).atualizar(
                argThat(p -> p.getStatus() == StatusProdutor.valueOf(status)));
    }

    @Então("o artista deve ser atualizado para o status {string}")
    public void oArtistaDeveSerAtualizadoParaOStatus(String status) {
        assertNull(excecaoCapturada, mensagemFalha());
        verify(artistaRepo, times(1)).atualizar(
                argThat(a -> a.getStatus() == StatusArtista.valueOf(status)));
    }

    // =========================================================================
    // ENTÃO — Rider Técnico
    // =========================================================================

    @Então("o rider técnico deve ser atualizado com sucesso")
    public void oRiderTecnicoDeveSerAtualizadoComSucesso() {
        assertNull(excecaoCapturada, mensagemFalha());
        verify(artistaRepo, times(1)).atualizar(
                argThat(a -> !a.getRiderTecnico().itens().isEmpty()));
    }

    // =========================================================================
    // ENTÃO — Anonimização LGPD
    // =========================================================================

    @Então("os dados de e-mail e telefone devem ser anonimizados")
    public void osDadosDeEmailETelefoneDevemSerAnonimizados() {
        verify(produtorRepo, times(1)).atualizar(argThat(p ->
                "anonimizado@lgpd.recife.br".equals(p.getEmail()) && p.getTelefone() == null));
    }

    // =========================================================================
    // ENTÃO — Rejeições genéricas
    // =========================================================================

    @Então("a operação deve ser rejeitada")
    public void aOperacaoDeveSerRejeitada() {
        assertNotNull(excecaoCapturada, "Esperava rejeição, mas a operação teve sucesso.");
    }

    @Então("o cadastro deve ser rejeitado")
    public void oCadastroDeveSerRejeitado() {
        assertNotNull(excecaoCapturada, "Esperava rejeição do cadastro, mas ele teve sucesso.");
    }

    // =========================================================================
    // ENTÃO — Mensagens de validação
    // =========================================================================

    @Então("a validação deve informar que o CNPJ é inválido")
    public void aValidacaoDeveInformarQueCnpjEhInvalido() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toUpperCase().contains("CNPJ"),
                "Esperava mensagem sobre CNPJ inválido. Obtido: " + excecaoCapturada.getMessage());
    }

    @Então("a validação deve informar que o CNPJ já existe")
    public void aValidacaoDeveInformarQueCnpjJaExiste() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toUpperCase().contains("CNPJ"),
                "Esperava mensagem sobre CNPJ duplicado. Obtido: " + excecaoCapturada.getMessage());
    }

    @Então("a validação deve informar que o produtor deve estar ativo")
    public void aValidacaoDeveInformarQueProdutorDeveEstarAtivo() {
        assertNotNull(excecaoCapturada);
        String msg = excecaoCapturada.getMessage().toLowerCase();
        assertTrue(msg.contains("produtor") || msg.contains("inativo") || msg.contains("suspenso"),
                "Esperava mensagem sobre status do produtor. Obtido: " + excecaoCapturada.getMessage());
    }

    @Então("a validação deve informar que já existe artista com esse nome para o produtor")
    public void aValidacaoDeveInformarQueJaExisteArtistaComEsseNome() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toLowerCase().contains("artista"),
                "Esperava mensagem sobre artista duplicado. Obtido: " + excecaoCapturada.getMessage());
    }

    @Então("a validação deve informar que existem artistas ativos vinculados")
    public void aValidacaoDeveInformarQueExistemArtistasAtivosVinculados() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toLowerCase().contains("artista"),
                "Esperava mensagem sobre artistas ativos. Obtido: " + excecaoCapturada.getMessage());
    }

    @Então("a validação deve informar que artista inativo não pode atualizar o rider técnico")
    public void aValidacaoDeveInformarQueArtistaInativoNaoPodeAtualizarRider() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toLowerCase().contains("inativo"),
                "Esperava mensagem sobre artista inativo. Obtido: " + excecaoCapturada.getMessage());
    }

    @Então("a validação deve informar que produtor inativo não pode ser reativado")
    public void aValidacaoDeveInformarQueProdutorInativoNaoPodeSerReativado() {
        assertNotNull(excecaoCapturada);
        assertTrue(excecaoCapturada.getMessage().toLowerCase().contains("inativo"),
                "Esperava mensagem sobre produtor inativo. Obtido: " + excecaoCapturada.getMessage());
    }

    // =========================================================================
    // Utilitário
    // =========================================================================

    private String mensagemFalha() {
        return excecaoCapturada != null
                ? "Operação inesperadamente rejeitada: " + excecaoCapturada.getMessage()
                : null;
    }
}
