package recifecultural.dominio.cupom;

import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import recifecultural.dominio.cupom.validacoes.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PassosCupom {

    // 1. O Padrão Decorator montado para os testes do Cucumber!
    private final ValidadorCupom validadorPipeline =
            new ValidarVigenciaDecorator(
                    new ValidarMinimoDecorator(
                            new ValidarCategoriaDecorator(
                                    new ValidarEscassezGlobalDecorator(
                                            new ValidarLimiteCpfDecorator(
                                                    new ValidadorCupomBase()
                                            )
                                    )
                            )
                    )
            );

    private ICupomRepositorio repositorioMock = mock(ICupomRepositorio.class);
    private ContextoCupom contexto = new ContextoCupom();
    private Cupom cupomValido;

    @Dado("que o sistema possui o cupom {string} com {int}% de desconto")
    public void setupCupom(String codigo, Integer perc) {
        cupomValido = new Cupom(
                new CupomId("ID-" + codigo), codigo, TipoDesconto.PERCENTUAL,
                BigDecimal.valueOf(perc), BigDecimal.valueOf(100.0), 5, 1,
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 12, 31, 23, 59),
                "TEATRO"
        );

        when(repositorioMock.buscarPorCodigo(codigo)).thenReturn(cupomValido);
    }

    @Dado("que o sistema possui o cupom {string} com {double} reais de desconto fixo")
    public void setupCupomFixo(String codigo, Double valorFixo) {
        cupomValido = new Cupom(
                new CupomId("ID-" + codigo), codigo, TipoDesconto.VALOR_FIXO,
                BigDecimal.valueOf(valorFixo), BigDecimal.valueOf(100.0), 5, 1,
                LocalDateTime.of(2026, 1, 1, 0, 0),
                LocalDateTime.of(2026, 12, 31, 23, 59),
                "TEATRO"
        );

        when(repositorioMock.buscarPorCodigo(codigo)).thenReturn(cupomValido);
    }

    @Dado("que o espectador com CPF {string} já utilizou o cupom {string} {int} vez")
    public void espectador_ja_utilizou(String cpf, String codigo, Integer vezes) {
        for(int i = 0; i < vezes; i++) {
            cupomValido.registrarUso(cpf);
        }
    }

    @Dado("que o cupom {string} já foi utilizado {int} vezes no total")
    public void cupom_utilizado_total(String codigo, Integer vezes) {
        for(int i = 0; i < vezes; i++) {
            cupomValido.registrarUso("CPF-ALEATORIO-PARA-GASTAR-" + i);
        }
    }

    @Quando("o espectador com CPF {string} tenta aplicar o cupom {string} em um ingresso de {string} no valor de {double} reais na data {string}")
    public void aplicarCupomComData(String cpf, String codigo, String categoria, Double valor, String dataString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDateTime dataCompra = LocalDate.parse(dataString, formatter).atStartOfDay();

        try {
            BigDecimal valorBd = BigDecimal.valueOf(valor);

            cupomValido.validarElegibilidade(cpf, valorBd, categoria, dataCompra, validadorPipeline);

            BigDecimal desconto = cupomValido.calcularDesconto(valorBd);
            contexto.valorCalculado = valorBd.subtract(desconto).doubleValue();

            contexto.excecao = null;

            repositorioMock.salvar(cupomValido);

        } catch (Exception e) {
            contexto.excecao = e;
        }
    }

    @Quando("o espectador com CPF {string} tenta aplicar o cupom {string} em um ingresso de {string} no valor de {double} reais")
    public void aplicarCupomSemData(String cpf, String codigo, String categoria, Double valor) {
        aplicarCupomComData(cpf, codigo, categoria, valor, "15/06/2026");
    }

    @Então("o sistema aplica o desconto")
    public void verificaSucesso() {
        Assertions.assertNull(contexto.excecao, "Não deveria ter ocorrido nenhum erro de validação.");
        verify(repositorioMock, times(1)).salvar(cupomValido);
    }

    @Então("o valor final da compra deve ser {double} reais")
    public void verificaValor(Double esperado) {
        Assertions.assertEquals(esperado, contexto.valorCalculado, 0.01);
    }

    @Então("o sistema deve negar a aplicação com o erro {string}")
    public void verificaErro(String erroEsperado) {
        Assertions.assertNotNull(contexto.excecao, "Era esperada uma exceção de validação.");
        Assertions.assertEquals(erroEsperado, contexto.excecao.getMessage());
        verify(repositorioMock, never()).salvar(any(Cupom.class));
    }
}