package recifecultural.dominio.patrocinio;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SubsidioIngressoFuncionalidade {

    private final PatrocinioContexto ctx;

    public SubsidioIngressoFuncionalidade(PatrocinioContexto ctx) {
        this.ctx = ctx;
    }

    @Given("que existe um patrocínio ATIVO de SUBSIDIO_INGRESSO_SOCIAL com valor {bigdecimal}")
    public void que_existe_patrocinio_ativo_subsidio(BigDecimal valor) {
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Social", "ONG",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL,
                valor, LocalDateTime.now().plusDays(30), true);
        ctx.patrocinioId = patrocinio.getId();
        ctx.servico.ativar(ctx.patrocinioId);
    }

    @Given("que existe um patrocínio ATIVO de FINANCEIRO com valor {bigdecimal}")
    public void que_existe_patrocinio_ativo_financeiro(BigDecimal valor) {
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Financeiro", "Bancos",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                valor, LocalDateTime.now().plusDays(30), true);
        ctx.patrocinioId = patrocinio.getId();
        ctx.servico.ativar(ctx.patrocinioId);
    }

    @Given("que existe um patrocínio CANCELADO de SUBSIDIO_INGRESSO_SOCIAL com valor {bigdecimal}")
    public void que_existe_patrocinio_cancelado_subsidio(BigDecimal valor) {
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Social", "ONG",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL,
                valor, LocalDateTime.now().plusDays(30), true);
        ctx.patrocinioId = patrocinio.getId();
        ctx.servico.ativar(ctx.patrocinioId);
        ctx.servico.cancelarPorEvento(ctx.patrocinioId, LocalDateTime.now());
    }

    @When("calculo o subsídio para um ingresso social com preço {bigdecimal}")
    public void calculo_o_subsidio(BigDecimal preco) {
        try {
            ctx.resultadoSubsidio = ctx.servico.calcularSubsidio(ctx.patrocinioId, preco);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento calcular o subsídio para um ingresso social com preço {bigdecimal}")
    public void tento_calcular_o_subsidio(BigDecimal preco) {
        try {
            ctx.resultadoSubsidio = ctx.servico.calcularSubsidio(ctx.patrocinioId, preco);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Then("o novo preço social é {bigdecimal}")
    public void o_novo_preco_social_e(BigDecimal esperado) {
        assertNotNull(ctx.resultadoSubsidio);
        assertEquals(0, esperado.compareTo(ctx.resultadoSubsidio.getNovoPrecoSocial()));
    }

    @And("o piso não foi aplicado")
    public void o_piso_nao_foi_aplicado() {
        assertFalse(ctx.resultadoSubsidio.isPisoAplicado());
    }

    @And("o piso foi aplicado")
    public void o_piso_foi_aplicado() {
        assertTrue(ctx.resultadoSubsidio.isPisoAplicado());
    }
}
