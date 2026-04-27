package recifecultural.dominio.patrocinio;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CancelarPatrocinioFuncionalidade {

    private final PatrocinioContexto ctx;

    public CancelarPatrocinioFuncionalidade(PatrocinioContexto ctx) {
        this.ctx = ctx;
    }

    @Given("que existe um patrocínio ATIVO para um evento daqui a {int} dias com valor {bigdecimal}")
    public void que_existe_um_patrocinio_ativo(int dias, BigDecimal valor) {
        LocalDateTime dataEvento = LocalDateTime.now().plusDays(dias);
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Teste", "Bebidas",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                valor, dataEvento, true);
        ctx.patrocinioId = patrocinio.getId();
        ctx.servico.ativar(ctx.patrocinioId);
    }

    @Given("que existe um patrocínio ENCERRADO")
    public void que_existe_um_patrocinio_encerrado() {
        LocalDateTime dataEvento = LocalDateTime.now().plusDays(30);
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Teste", "Bebidas",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.FINANCEIRO,
                new BigDecimal("5000.00"), dataEvento, true);
        ctx.patrocinioId = patrocinio.getId();
        ctx.servico.ativar(ctx.patrocinioId);
        ctx.servico.encerrar(ctx.patrocinioId);
    }

    @When("o evento cancela o patrocínio")
    public void o_evento_cancela_o_patrocinio() {
        try {
            ctx.resultadoCancelamento = ctx.servico.cancelarPorEvento(ctx.patrocinioId, LocalDateTime.now());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("o patrocinador cancela o patrocínio")
    public void o_patrocinador_cancela_o_patrocinio() {
        try {
            ctx.resultadoCancelamento = ctx.servico.cancelarPorPatrocinador(ctx.patrocinioId, LocalDateTime.now());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("o evento tenta cancelar o patrocínio encerrado")
    public void o_evento_tenta_cancelar_o_patrocinio_encerrado() {
        try {
            ctx.resultadoCancelamento = ctx.servico.cancelarPorEvento(ctx.patrocinioId, LocalDateTime.now());
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @Then("o resultado do cancelamento tem reembolso de {bigdecimal}")
    public void o_resultado_tem_reembolso_de(BigDecimal esperado) {
        assertNotNull(ctx.resultadoCancelamento);
        assertEquals(0, esperado.compareTo(ctx.resultadoCancelamento.getValorReembolsado()));
    }

    @And("a multa aplicada é {bigdecimal}")
    public void a_multa_aplicada_e(BigDecimal esperado) {
        assertNotNull(ctx.resultadoCancelamento);
        assertEquals(0, esperado.compareTo(ctx.resultadoCancelamento.getMultaAplicada()));
    }

    @And("o status do patrocínio é {string}")
    public void o_status_do_patrocinio_e(String status) {
        Patrocinio patrocinio = ctx.repositorio.buscarPorId(ctx.patrocinioId);
        assertEquals(StatusPatrocinio.valueOf(status), patrocinio.getStatus());
    }
}
