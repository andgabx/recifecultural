package recifecultural.dominio.ingressos;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import recifecultural.dominio.cupom.AplicarCupomServico;
import recifecultural.dominio.cupom.Cupom;
import recifecultural.dominio.cupom.CupomId;
import recifecultural.dominio.cupom.ICupomRepositorio;
import recifecultural.dominio.cupom.TipoDesconto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ComprarComPreReservaFuncionalidade extends IngressoFuncionalidade {

    private static final LocalDateTime DATA_APRESENTACAO = LocalDateTime.now().plusDays(30);
    private static final UUID EVENTO_ID = UUID.randomUUID();

    private UUID preReservaId;
    private UUID assentoId;
    private IConfirmacaoReserva confirmacaoReservaMock;
    private ICupomRepositorio cupomRepositorioMock;

    private Ingresso ingressoComprado;
    private RuntimeException excecao;

    public ComprarComPreReservaFuncionalidade() {
        super();
        confirmacaoReservaMock = mock(IConfirmacaoReserva.class);
    }

    @Given("que existe uma pré-reserva válida com id {string} para o assento {string}")
    public void que_existe_uma_pre_reserva_valida(String reservaIdStr, String assentoIdStr) {
        preReservaId = UUID.nameUUIDFromBytes(reservaIdStr.getBytes());
        assentoId = UUID.nameUUIDFromBytes(assentoIdStr.getBytes());
    }

    @Given("que existe um cupom {string} com {int} por cento de desconto válido para a categoria {string}")
    public void que_existe_um_cupom_percentual(String codigo, int percentual, String categoria) {
        Cupom cupom = new Cupom(
                new CupomId("ID-" + codigo), codigo, TipoDesconto.PERCENTUAL,
                new BigDecimal(percentual), new BigDecimal("100.00"),
                10, 1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(30),
                categoria);

        cupomRepositorioMock = mock(ICupomRepositorio.class);
        when(cupomRepositorioMock.buscarPorCodigo(codigo)).thenReturn(cupom);

        AplicarCupomServico cupomServico = new AplicarCupomServico(cupomRepositorioMock);
        servico = new IngressoServico(repositorio, gateway, null, cupomServico);
    }

    @Given("que o gateway rejeita pagamentos na compra com pré-reserva")
    public void que_o_gateway_rejeita_pagamentos_na_compra_com_pre_reserva() {
        gateway = mock(IGatewayPagamento.class);
        when(gateway.processar(any(), any(), any()))
                .thenReturn(new ResultadoPagamento("", false));
        servico = new IngressoServico(repositorio, gateway);
    }

    @When("submeto a compra com pré-reserva {word} via {word} com valor {bigdecimal} e capacidade {int}")
    public void submeto_a_compra_com_pre_reserva(String tipo, String metodo, BigDecimal valor, int capacidade) {
        try {
            ingressoComprado = servico.comprarComPreReserva(
                    EVENTO_ID, DATA_APRESENTACAO,
                    preReservaId, assentoId,
                    TipoIngresso.valueOf(tipo),
                    valor,
                    MetodoPagamento.valueOf(metodo),
                    capacidade,
                    confirmacaoReservaMock);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @When("submeto a compra com pré-reserva {word} via {word} com valor {bigdecimal} cupom {string} CPF {string} categoria {string} e capacidade {int}")
    public void submeto_a_compra_com_pre_reserva_com_cupom(String tipo, String metodo, BigDecimal valor,
                                                            String codigoCupom, String cpf, String categoria,
                                                            int capacidade) {
        try {
            ingressoComprado = servico.comprarComPreReservaComCupom(
                    EVENTO_ID, DATA_APRESENTACAO,
                    preReservaId, assentoId,
                    TipoIngresso.valueOf(tipo),
                    valor,
                    MetodoPagamento.valueOf(metodo),
                    capacidade,
                    codigoCupom, cpf, categoria,
                    confirmacaoReservaMock);
        } catch (RuntimeException e) {
            excecao = e;
        }
    }

    @Then("o ingresso com pré-reserva é criado com status {string}")
    public void o_ingresso_com_pre_reserva_e_criado_com_status(String status) {
        assertNotNull(ingressoComprado);
        assertEquals(StatusIngresso.valueOf(status), ingressoComprado.getStatus());
    }

    @Then("o ingresso com pré-reserva possui um QR code único")
    public void o_ingresso_com_pre_reserva_possui_qr_code_unico() {
        assertNotNull(ingressoComprado.getCodigoQr());
        assertFalse(ingressoComprado.getCodigoQr().isBlank());
    }

    @Then("o valor pago com pré-reserva é {bigdecimal}")
    public void o_valor_pago_com_pre_reserva_e(BigDecimal valor) {
        assertNotNull(ingressoComprado);
        assertEquals(0, valor.compareTo(ingressoComprado.getValorPago()));
    }

    @Then("a pré-reserva {string} foi confirmada")
    public void a_pre_reserva_foi_confirmada(String reservaIdStr) {
        UUID expectedId = UUID.nameUUIDFromBytes(reservaIdStr.getBytes());
        verify(confirmacaoReservaMock, times(1)).confirmar(expectedId);
    }

    @Then("a pré-reserva {string} foi cancelada")
    public void a_pre_reserva_foi_cancelada(String reservaIdStr) {
        UUID expectedId = UUID.nameUUIDFromBytes(reservaIdStr.getBytes());
        verify(confirmacaoReservaMock, times(1)).cancelar(expectedId);
    }

    @Then("o sistema rejeita a compra com pré-reserva com a mensagem {string}")
    public void o_sistema_rejeita_a_compra_com_pre_reserva_com_a_mensagem(String mensagem) {
        assertNotNull(excecao);
        assertEquals(mensagem, excecao.getMessage());
    }
}
