package recifecultural.dominio.ingressos;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ComprarIngressoFuncionalidade extends IngressoFuncionalidade {

    private static final UUID EVENTO_ID = UUID.randomUUID();
    private static final LocalDateTime DATA_APRESENTACAO = LocalDateTime.now().plusDays(30);

    private Ingresso ingressoComprado;
    private RuntimeException excecao;

    @Given("que a apresentação já possui {int} ingressos ativos com capacidade máxima {int}")
    public void que_a_apresentacao_ja_possui_ingressos_ativos(int quantidade, int capacidade) {
        for (int i = 0; i < quantidade; i++) {
            servico.comprar(EVENTO_ID, DATA_APRESENTACAO, TipoIngresso.INTEIRA,
                    new BigDecimal("100.00"), MetodoPagamento.PIX, capacidade + 1);
        }
    }

    @Given("que o gateway rejeita pagamentos")
    public void que_o_gateway_rejeita_pagamentos() {
        gateway = mock(IGatewayPagamento.class);
        when(gateway.processar(any(), any(), any()))
                .thenReturn(new ResultadoPagamento("", false));
        servico = new IngressoServico(repositorio, gateway);
    }

    @When("submeto a compra de um ingresso {word} via {word} com valor {bigdecimal} e capacidade {int}")
    public void submeto_a_compra(String tipo, String metodo, BigDecimal valor, int capacidade) {
        try {
            ingressoComprado = servico.comprar(EVENTO_ID, DATA_APRESENTACAO,
                    TipoIngresso.valueOf(tipo), valor,
                    MetodoPagamento.valueOf(metodo), capacidade);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("tento comprar mais um ingresso {word} via {word} com valor {bigdecimal}")
    public void tento_comprar_mais_um_ingresso(String tipo, String metodo, BigDecimal valor) {
        try {
            ingressoComprado = servico.comprar(EVENTO_ID, DATA_APRESENTACAO,
                    TipoIngresso.valueOf(tipo), valor,
                    MetodoPagamento.valueOf(metodo), 50);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o ingresso é criado com status {string}")
    public void o_ingresso_e_criado_com_status(String status) {
        assertNotNull(ingressoComprado);
        assertEquals(StatusIngresso.valueOf(status), ingressoComprado.getStatus());
    }

    @Then("o ingresso possui um QR code único")
    public void o_ingresso_possui_qr_code_unico() {
        assertNotNull(ingressoComprado.getCodigoQr());
        assertFalse(ingressoComprado.getCodigoQr().isBlank());
    }

    @Then("o valor pago é {bigdecimal}")
    public void o_valor_pago_e(BigDecimal valor) {
        assertNotNull(ingressoComprado);
        assertEquals(0, valor.compareTo(ingressoComprado.getValorPago()));
    }

    @Then("o sistema rejeita a compra com a mensagem {string}")
    public void o_sistema_rejeita_a_compra_com_a_mensagem(String mensagem) {
        assertNotNull(excecao);
        assertEquals(mensagem, excecao.getMessage());
    }
}
