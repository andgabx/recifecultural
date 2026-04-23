package recifecultural.dominio.financeiro;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import recifecultural.dominio.ingressos.Ingresso;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.TipoIngresso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class DesempenhoTeatroFuncionalidade {

    private final FinanceiroContexto ctx;

    public DesempenhoTeatroFuncionalidade(FinanceiroContexto ctx) {
        this.ctx = ctx;
    }

    @Given("que existe um orçamento de {bigdecimal} para análise de desempenho")
    public void que_existe_um_orcamento_para_analise_de_desempenho(BigDecimal valor) {
        ctx.periodoAtual = new Periodo(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().plusDays(1));
        OrcamentoPeriodo orcamento = new OrcamentoPeriodo(OrcamentoId.novo(), ctx.periodoAtual, valor);
        ctx.orcamentoRepositorio.salvar(orcamento);
        ctx.orcamentoId = orcamento.getId();
    }

    @Given("foram vendidos {int} ingressos com receita bruta de {bigdecimal} no período")
    public void foram_vendidos_ingressos_com_receita_bruta(int quantidade, BigDecimal receitaBruta) {
        BigDecimal valorUnitario = receitaBruta.divide(new BigDecimal(quantidade), 2, RoundingMode.HALF_UP);
        List<Ingresso> ingressosMock = IntStream.range(0, quantidade)
                .mapToObj(i -> new Ingresso(
                        IngressoId.novo(),
                        UUID.randomUUID(),
                        LocalDateTime.now().plusDays(5),
                        TipoIngresso.INTEIRA,
                        valorUnitario,
                        UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        MetodoPagamento.PIX))
                .toList();
        when(ctx.ingressoRepositorio.buscarPorPeriodo(any(), any())).thenReturn(ingressosMock);
    }

    @Given("foram registradas despesas de {bigdecimal} no orçamento")
    public void foram_registradas_despesas_no_orcamento(BigDecimal valor) {
        Despesa despesa = new Despesa(DespesaId.novo(), ctx.orcamentoId, "Despesa operacional", valor, CategoriaDespesa.PESSOAL);
        ctx.despesaRepositorio.salvar(despesa);
    }

    @Given("que existem dados para dois períodos distintos sem sobreposição")
    public void que_existem_dados_para_dois_periodos_distintos() {
        ctx.periodoAnterior = new Periodo(
                LocalDateTime.now().minusDays(60),
                LocalDateTime.now().minusDays(31));
        ctx.periodoAtual = new Periodo(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().plusDays(1));
        ctx.orcamentoRepositorio.salvar(new OrcamentoPeriodo(OrcamentoId.novo(), ctx.periodoAnterior, new BigDecimal("5000.00")));
        ctx.orcamentoRepositorio.salvar(new OrcamentoPeriodo(OrcamentoId.novo(), ctx.periodoAtual, new BigDecimal("6000.00")));
        when(ctx.ingressoRepositorio.buscarPorPeriodo(any(), any())).thenReturn(List.of());
    }

    @Given("que existem dois períodos que se sobrepõem")
    public void que_existem_dois_periodos_que_se_sobropoem() {
        ctx.periodoSobrepostoA = new Periodo(
                LocalDateTime.now().minusDays(10),
                LocalDateTime.now().plusDays(20));
        ctx.periodoSobrepostoB = new Periodo(
                LocalDateTime.now().minusDays(5),
                LocalDateTime.now().plusDays(25));
    }

    @When("calculo os indicadores de desempenho com capacidade total de {int}")
    public void calculo_os_indicadores_de_desempenho(int capacidade) {
        try {
            ctx.indicadores = ctx.servico.calcularIndicadores(ctx.periodoAtual, capacidade);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("comparo os dois períodos com capacidade total de {int}")
    public void comparo_os_dois_periodos(int capacidade) {
        try {
            ctx.comparativo = ctx.servico.compararPeriodos(ctx.periodoAnterior, ctx.periodoAtual, capacidade);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento comparar os períodos sobrepostos com capacidade total de {int}")
    public void tento_comparar_os_periodos_sobrepostos(int capacidade) {
        try {
            ctx.comparativo = ctx.servico.compararPeriodos(ctx.periodoSobrepostoA, ctx.periodoSobrepostoB, capacidade);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Then("a taxa de ocupação é {bigdecimal}")
    public void a_taxa_de_ocupacao_e(BigDecimal taxa) {
        assertNotNull(ctx.indicadores);
        assertEquals(0, taxa.compareTo(ctx.indicadores.getTaxaOcupacao()));
    }

    @Then("a receita bruta é {bigdecimal}")
    public void a_receita_bruta_e(BigDecimal valor) {
        assertNotNull(ctx.indicadores);
        assertEquals(0, valor.compareTo(ctx.indicadores.getReceitaBruta()));
    }

    @Then("a receita líquida é {bigdecimal}")
    public void a_receita_liquida_e(BigDecimal valor) {
        assertNotNull(ctx.indicadores);
        assertEquals(0, valor.compareTo(ctx.indicadores.getReceitaLiquida()));
    }

    @Then("o comparativo retorna indicadores para ambos os períodos")
    public void o_comparativo_retorna_indicadores_para_ambos_os_periodos() {
        assertNotNull(ctx.comparativo);
        assertNotNull(ctx.comparativo.getPeriodoAnterior());
        assertNotNull(ctx.comparativo.getPeriodoAtual());
    }
}
