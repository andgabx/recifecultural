package recifecultural.dominio.ingressos;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ReembolsarIngressoFuncionalidade extends IngressoFuncionalidade {

    private static final UUID EVENTO_ID = UUID.randomUUID();

    private IngressoId ingressoId;
    private ResultadoReembolso resultadoReembolso;
    private RuntimeException excecao;

    @Given("que possuo um ingresso ATIVO via {word} com valor {bigdecimal} para daqui a {int} dias")
    public void que_possuo_um_ingresso_ativo(String metodo, BigDecimal valor, int diasAntecedencia) {
        LocalDateTime dataApresentacao = LocalDateTime.now().plusDays(diasAntecedencia);
        Ingresso ingresso = servico.comprar(EVENTO_ID, dataApresentacao,
                TipoIngresso.INTEIRA, valor,
                MetodoPagamento.valueOf(metodo), 100);
        ingressoId = ingresso.getId();
    }

    @Given("que possuo um ingresso já REEMBOLSADO")
    public void que_possuo_um_ingresso_ja_reembolsado() {
        LocalDateTime dataApresentacao = LocalDateTime.now().plusDays(10);
        Ingresso ingresso = servico.comprar(EVENTO_ID, dataApresentacao,
                TipoIngresso.INTEIRA, new BigDecimal("100.00"),
                MetodoPagamento.PIX, 100);
        ingressoId = ingresso.getId();
        servico.solicitarReembolso(ingressoId, LocalDateTime.now());
    }

    @Given("que possuo um ingresso com status UTILIZADO")
    public void que_possuo_um_ingresso_utilizado() {
        LocalDateTime dataApresentacao = LocalDateTime.now().plusDays(10);
        Ingresso ingresso = servico.comprar(EVENTO_ID, dataApresentacao,
                TipoIngresso.INTEIRA, new BigDecimal("100.00"),
                MetodoPagamento.PIX, 100);
        ingressoId = ingresso.getId();
        repositorio.buscarPorId(ingressoId).marcarComoUtilizado();
    }

    @When("solicito o reembolso")
    public void solicito_o_reembolso() {
        try {
            resultadoReembolso = servico.solicitarReembolso(ingressoId, LocalDateTime.now());
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o ingresso passa para o status {string}")
    public void o_ingresso_passa_para_o_status(String status) {
        Ingresso ingresso = repositorio.buscarPorId(ingressoId);
        assertNotNull(ingresso);
        assertEquals(StatusIngresso.valueOf(status), ingresso.getStatus());
    }

    @Then("o valor reembolsado é {bigdecimal}")
    public void o_valor_reembolsado_e(BigDecimal valor) {
        Ingresso ingresso = repositorio.buscarPorId(ingressoId);
        assertNotNull(ingresso);
        assertEquals(0, valor.compareTo(ingresso.getValorReembolsado()));
    }

    @Then("o prazo de processamento é {string}")
    public void o_prazo_de_processamento_e(String prazo) {
        assertNotNull(resultadoReembolso);
        assertEquals(prazo, resultadoReembolso.getPrazoProcessamento());
    }

    @Then("o sistema rejeita o reembolso com a mensagem {string}")
    public void o_sistema_rejeita_o_reembolso_com_a_mensagem(String mensagem) {
        assertNotNull(excecao);
        assertEquals(mensagem, excecao.getMessage());
    }
}
