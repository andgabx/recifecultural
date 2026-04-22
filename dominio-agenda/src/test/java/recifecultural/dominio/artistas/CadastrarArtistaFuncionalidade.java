package recifecultural.dominio.artistas;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CadastrarArtistaFuncionalidade extends ArtistaFuncionalidade {

    private Artista artista;
    private RuntimeException excecao;

    @Given("que já existe um artista cadastrado com o nome {string}")
    public void que_ja_existe_um_artista_cadastrado_com_o_nome(String nome) {
        servico.cadastrar(nome, "Bio existente", "existente@exemplo.com", "");
    }

    @When("submeto meu cadastro com nome {string}, bio {string}, email {string} e telefone {string}")
    public void submeto_meu_cadastro(String nome, String bio, String email, String telefone) {
        try {
            artista = servico.cadastrar(nome, bio, email, telefone);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("meu cadastro é criado com status {string}")
    public void meu_cadastro_e_criado_com_status(String status) {
        assertNotNull(artista);
        assertEquals(StatusArtista.valueOf(status), artista.getStatus());
    }

    @Then("o sistema rejeita o cadastro com a mensagem {string}")
    public void o_sistema_rejeita_o_cadastro_com_a_mensagem(String mensagem) {
        assertNotNull(excecao);
        assertEquals(mensagem, excecao.getMessage());
    }
}
