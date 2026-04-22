package recifecultural.dominio.artistas;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AprovarRejeitarArtistaFuncionalidade extends ArtistaFuncionalidade {

    private static final UUID PROMOTOR_ID = UUID.randomUUID();

    private ArtistId artistaId;
    private RuntimeException excecao;

    @Given("que existe um artista com status {string}")
    public void que_existe_um_artista_com_status(String status) {
        var artista = servico.cadastrar("Artista Teste", "Bio teste", "teste@exemplo.com", "");
        artistaId = artista.getId();

        switch (StatusArtista.valueOf(status)) {
            case APROVADO -> servico.aprovar(artistaId, PROMOTOR_ID);
            case REJEITADO -> servico.rejeitar(artistaId, PROMOTOR_ID, "Motivo inicial");
            default -> {}
        }
    }

    @When("o promotor aprova o artista")
    public void o_promotor_aprova_o_artista() {
        try {
            servico.aprovar(artistaId, PROMOTOR_ID);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("o promotor rejeita o artista com o motivo {string}")
    public void o_promotor_rejeita_o_artista_com_o_motivo(String motivo) {
        try {
            servico.rejeitar(artistaId, PROMOTOR_ID, motivo);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("o artista resubmete o cadastro")
    public void o_artista_resubmete_o_cadastro() {
        try {
            servico.resubmeter(artistaId);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o artista passa para o status {string}")
    public void o_artista_passa_para_o_status(String status) {
        var artista = repositorio.buscarPorId(artistaId);
        assertNotNull(artista);
        assertEquals(StatusArtista.valueOf(status), artista.getStatus());
    }

    @Then("o motivo de rejeição é {string}")
    public void o_motivo_de_rejeicao_e(String motivo) {
        var artista = repositorio.buscarPorId(artistaId);
        assertNotNull(artista);
        assertEquals(motivo, artista.getMotivoRejeicao());
    }

    @Then("o motivo de rejeição é removido")
    public void o_motivo_de_rejeicao_e_removido() {
        var artista = repositorio.buscarPorId(artistaId);
        assertNotNull(artista);
        assertNull(artista.getMotivoRejeicao());
    }

    @Then("o sistema rejeita a operação com a mensagem {string}")
    public void o_sistema_rejeita_a_operacao_com_a_mensagem(String mensagem) {
        assertNotNull(excecao);
        assertEquals(mensagem, excecao.getMessage());
    }
}
