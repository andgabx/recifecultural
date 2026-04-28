package recifecultural.dominio.cupom;

import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import static org.mockito.Mockito.*;


import recifecultural.dominio.ingressos.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class PassosIntegracao {
    private ICupomRepositorio cupomRepoMock = mock(ICupomRepositorio.class);
    private AplicarCupomServico cupomServico = new AplicarCupomServico(cupomRepoMock);

    private IngressoRepositorioEmMemoria ingressoRepo = new IngressoRepositorioEmMemoria();
    private IGatewayPagamento gateway = new GatewayPagamentoMock();
    private IngressoServico ingressoServico = new IngressoServico(ingressoRepo, gateway);

    private Ingresso ingressoComprado;
    private Exception excecaoCapturada;

    @Quando("o espectador com CPF {string} compra um ingresso da categoria {string} com valor base de {double} reais usando o cupom {string}")
    public void realizarCompra(String cpf, String categoria, Double valorBase, String codigoCupom) {
        try {
            Cupom cupom = new Cupom(
                    new CupomId("ID-" + codigoCupom), codigoCupom, 20, 100.0, 5, 1,
                    LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(10), categoria
            );
            when(cupomRepoMock.buscarPorCodigo(codigoCupom)).thenReturn(cupom);

            double valorFinalCalculado = cupomServico.aplicarDesconto(codigoCupom, cpf, valorBase, categoria);

            BigDecimal valorParaPagar = BigDecimal.valueOf(valorFinalCalculado);

            ingressoComprado = ingressoServico.comprar(
                    UUID.randomUUID(),
                    LocalDateTime.now().plusDays(5),
                    TipoIngresso.INTEIRA,
                    valorParaPagar,
                    MetodoPagamento.PIX,
                    100
            );

            excecaoCapturada = null;

        } catch (Exception e) {
            excecaoCapturada = e;
        }
    }

    @Então("o sistema valida o desconto com sucesso")
    public void verificaSucesso() {
        Assertions.assertNull(excecaoCapturada, "Ocorreu um erro: " +
                (excecaoCapturada != null ? excecaoCapturada.getMessage() : ""));
    }

    @Então("o ingresso deve ser salvo no banco de dados com o valor final pago de {double} reais")
    public void verificaIngressoSalvo(Double valorEsperado) {
        Assertions.assertNotNull(ingressoComprado, "O ingresso não foi gerado pelo serviço!");

        Ingresso ingressoNoBanco = ingressoRepo.buscarPorId(ingressoComprado.getId());
        Assertions.assertNotNull(ingressoNoBanco, "O ingresso não foi encontrado no banco de dados!");

        BigDecimal bdEsperado = BigDecimal.valueOf(valorEsperado);

        Assertions.assertEquals(0, bdEsperado.compareTo(ingressoNoBanco.getValorPago()),
                "O valor salvo no banco (" + ingressoNoBanco.getValorPago() + ") está diferente do esperado (" + bdEsperado + ")!");
    }
}