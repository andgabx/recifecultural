package recifecultural.dominio.financeiro;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrarDespesaFuncionalidade {

    private final FinanceiroContexto ctx;

    public RegistrarDespesaFuncionalidade(FinanceiroContexto ctx) {
        this.ctx = ctx;
    }

    @Given("que existe um orçamento de {bigdecimal} para o período atual")
    public void que_existe_um_orcamento_para_o_periodo_atual(BigDecimal valor) {
        Periodo periodo = new Periodo(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(30));
        OrcamentoPeriodo orcamento = new OrcamentoPeriodo(OrcamentoId.novo(), periodo, valor);
        ctx.orcamentoRepositorio.salvar(orcamento);
        ctx.orcamentoId = orcamento.getId();
    }

    @Given("que existe um orçamento de {bigdecimal} para um período passado")
    public void que_existe_um_orcamento_para_um_periodo_passado(BigDecimal valor) {
        Periodo periodo = new Periodo(
                LocalDateTime.now().minusDays(60),
                LocalDateTime.now().minusDays(30));
        OrcamentoPeriodo orcamento = new OrcamentoPeriodo(OrcamentoId.novo(), periodo, valor);
        ctx.orcamentoRepositorio.salvar(orcamento);
        ctx.orcamentoId = orcamento.getId();
    }

    @Given("que existe um orçamento encerrado de {bigdecimal} para o período atual")
    public void que_existe_um_orcamento_encerrado(BigDecimal valor) {
        Periodo periodo = new Periodo(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(30));
        OrcamentoPeriodo orcamento = new OrcamentoPeriodo(OrcamentoId.novo(), periodo, valor);
        orcamento.encerrar();
        ctx.orcamentoRepositorio.salvar(orcamento);
        ctx.orcamentoId = orcamento.getId();
    }

    @Given("já foram registradas despesas no valor de {bigdecimal}")
    public void ja_foram_registradas_despesas(BigDecimal valor) {
        Despesa despesa = new Despesa(DespesaId.novo(), ctx.orcamentoId, "Despesa existente", valor, CategoriaDespesa.OUTROS);
        ctx.despesaRepositorio.salvar(despesa);
    }

    @When("registro uma despesa de {bigdecimal} na categoria {word} com descrição {string}")
    public void registro_uma_despesa(BigDecimal valor, String categoria, String descricao) {
        try {
            ctx.resultado = ctx.servico.registrarDespesa(ctx.orcamentoId, descricao, valor, CategoriaDespesa.valueOf(categoria));
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("reduzo o orçamento para {bigdecimal}")
    public void reduzo_o_orcamento_para(BigDecimal novoValor) {
        try {
            OrcamentoPeriodo orcamento = ctx.orcamentoRepositorio.buscarPorId(ctx.orcamentoId);
            BigDecimal totalDespesas = ctx.despesaRepositorio.somarPorOrcamento(ctx.orcamentoId);
            orcamento.reduzir(novoValor, totalDespesas);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Then("a despesa é registrada com sucesso")
    public void a_despesa_e_registrada_com_sucesso() {
        assertNotNull(ctx.resultado);
        assertNotNull(ctx.resultado.getDespesa());
    }

    @Then("não há alerta de orçamento")
    public void nao_ha_alerta_de_orcamento() {
        assertFalse(ctx.resultado.isAlertaOrcamento());
    }

    @Then("há alerta de orçamento")
    public void ha_alerta_de_orcamento() {
        assertTrue(ctx.resultado.isAlertaOrcamento());
    }

    @Then("o sistema rejeita a operação com a mensagem {string}")
    public void o_sistema_rejeita_a_operacao_com_a_mensagem(String mensagem) {
        assertNotNull(ctx.excecao);
        assertEquals(mensagem, ctx.excecao.getMessage());
    }
}
