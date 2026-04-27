package recifecultural.dominio.patrocinio;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CriarPatrocinioFuncionalidade {

    private final PatrocinioContexto ctx;

    private static final LocalDateTime DATA_EVENTO_FUTURA = LocalDateTime.now().plusDays(30);

    public CriarPatrocinioFuncionalidade(PatrocinioContexto ctx) {
        this.ctx = ctx;
    }

    @Given("que existe um evento aprovado")
    public void que_existe_um_evento_aprovado() {
        ctx.eventoId = EventoId.novo();
    }

    @Given("que existe um evento não aprovado")
    public void que_existe_um_evento_nao_aprovado() {
        ctx.eventoId = EventoId.novo();
    }

    @Given("já existe um patrocínio MASTER da categoria {string}")
    public void ja_existe_um_patrocinio_master_da_categoria(String categoria) {
        Patrocinio master = ctx.servico.criar(
                ctx.eventoId, "Patrocinador Master", categoria,
                TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                new BigDecimal("5000.00"), DATA_EVENTO_FUTURA, true);
        ctx.patrocinioId = master.getId();
    }

    @Given("já existe um patrocínio ASSOCIADO da categoria {string}")
    public void ja_existe_um_patrocinio_associado_da_categoria(String categoria) {
        ctx.servico.criar(
                ctx.eventoId, "Patrocinador Associado", categoria,
                TipoPatrocinio.ASSOCIADO, ModalidadeContribuicao.FINANCEIRO,
                new BigDecimal("2000.00"), DATA_EVENTO_FUTURA, true);
    }

    @When("crio um patrocínio MASTER da categoria {string} com valor {bigdecimal}")
    public void crio_um_patrocinio_master(String categoria, BigDecimal valor) {
        try {
            ctx.patrocinio = ctx.servico.criar(
                    ctx.eventoId, "Patrocinador Teste", categoria,
                    TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                    valor, DATA_EVENTO_FUTURA, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("crio um patrocínio ASSOCIADO da categoria {string} com valor {bigdecimal}")
    public void crio_um_patrocinio_associado(String categoria, BigDecimal valor) {
        try {
            ctx.patrocinio = ctx.servico.criar(
                    ctx.eventoId, "Patrocinador Teste", categoria,
                    TipoPatrocinio.ASSOCIADO, ModalidadeContribuicao.FINANCEIRO,
                    valor, DATA_EVENTO_FUTURA, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento criar um segundo patrocínio MASTER da categoria {string} com valor {bigdecimal}")
    public void tento_criar_segundo_master(String categoria, BigDecimal valor) {
        try {
            ctx.servico.criar(
                    ctx.eventoId, "Segundo Master", categoria,
                    TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                    valor, DATA_EVENTO_FUTURA, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento criar um patrocínio ASSOCIADO da categoria {string} com valor {bigdecimal}")
    public void tento_criar_associado_categoria_duplicada(String categoria, BigDecimal valor) {
        try {
            ctx.servico.criar(
                    ctx.eventoId, "Patrocinador Duplicado", categoria,
                    TipoPatrocinio.ASSOCIADO, ModalidadeContribuicao.FINANCEIRO,
                    valor, DATA_EVENTO_FUTURA, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento criar um patrocínio MASTER da categoria {string} com valor {bigdecimal} no evento não aprovado")
    public void tento_criar_patrocinio_evento_nao_aprovado(String categoria, BigDecimal valor) {
        try {
            ctx.servico.criar(
                    ctx.eventoId, "Patrocinador Teste", categoria,
                    TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                    valor, DATA_EVENTO_FUTURA, false);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento criar um patrocínio MASTER da categoria {string} com valor {bigdecimal}")
    public void tento_criar_patrocinio_valor_zero(String categoria, BigDecimal valor) {
        try {
            ctx.servico.criar(
                    ctx.eventoId, "Patrocinador Teste", categoria,
                    TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                    valor, DATA_EVENTO_FUTURA, true);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Then("o patrocínio é criado com status {string}")
    public void o_patrocinio_e_criado_com_status(String status) {
        assertNull(ctx.excecao);
        assertNotNull(ctx.patrocinio);
        assertEquals(StatusPatrocinio.valueOf(status), ctx.patrocinio.getStatus());
    }

    @And("o tipo do patrocínio é {string}")
    public void o_tipo_do_patrocinio_e(String tipo) {
        assertEquals(TipoPatrocinio.valueOf(tipo), ctx.patrocinio.getTipo());
    }

    @Then("o sistema rejeita com a mensagem {string}")
    public void o_sistema_rejeita_com_a_mensagem(String mensagem) {
        assertNotNull(ctx.excecao);
        assertTrue(ctx.excecao.getMessage().contains(mensagem));
    }
}
