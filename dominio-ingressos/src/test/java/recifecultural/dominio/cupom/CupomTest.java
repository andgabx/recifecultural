package recifecultural.dominio.cupom;

import org.junit.jupiter.api.Test;
import recifecultural.dominio.cupom.validacoes.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CupomTest {
    private final List<ValidacaoCupomStrategy> estrategiasDeValidacao = List.of(
            new ValidarVigenciaStrategy(),
            new ValidarMinimoStrategy(),
            new ValidarCategoriaStrategy(),
            new ValidarEscassezGlobalStrategy(),
            new ValidarLimiteCpfStrategy()
    );

    private Cupom cupomPercentual(int percentual) {
        return new Cupom(
                new CupomId("ID-1"), "CUPOM-1", TipoDesconto.PERCENTUAL,
                new BigDecimal(percentual), new BigDecimal("100.00"),
                10, 1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(10),
                "TEATRO");
    }

    private Cupom cupomFixo(String valor) {
        return new Cupom(
                new CupomId("ID-2"), "CUPOM-2", TipoDesconto.VALOR_FIXO,
                new BigDecimal(valor), new BigDecimal("0.00"),
                10, 1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(10),
                null);
    }

    @Test
    void desconto_percentual_aplica_corretamente() {
        Cupom cupom = cupomPercentual(20);
        assertEquals(0, new BigDecimal("30.00").compareTo(cupom.calcularDesconto(new BigDecimal("150.00"))));
    }

    @Test
    void desconto_fixo_nao_passa_do_valor_original() {
        Cupom cupom = cupomFixo("100");
        assertEquals(0, new BigDecimal("50").compareTo(cupom.calcularDesconto(new BigDecimal("50"))));
    }

    @Test
    void valor_minimo_bloqueia_pedido_baixo() {
        Cupom cupom = cupomPercentual(20);
        assertThrows(IllegalArgumentException.class, () ->
                cupom.validarElegibilidade("CPF", new BigDecimal("80"), "TEATRO", LocalDateTime.now(), estrategiasDeValidacao));
    }

    @Test
    void categoria_diferente_bloqueia() {
        Cupom cupom = cupomPercentual(20);
        assertThrows(IllegalArgumentException.class, () ->
                cupom.validarElegibilidade("CPF", new BigDecimal("150"), "SHOW", LocalDateTime.now(), estrategiasDeValidacao));
    }

    @Test
    void cupom_fora_da_vigencia_bloqueia() {
        Cupom cupom = cupomPercentual(20);
        assertThrows(IllegalArgumentException.class, () ->
                cupom.validarElegibilidade("CPF", new BigDecimal("150"), "TEATRO", LocalDateTime.now().plusDays(20), estrategiasDeValidacao));
    }

    @Test
    void limite_global_bloqueia() {
        Cupom cupom = cupomPercentual(20);
        for (int i = 0; i < 10; i++) cupom.registrarUso("CPF-" + i);
        assertThrows(IllegalArgumentException.class, () ->
                cupom.validarElegibilidade("CPF-NOVO", new BigDecimal("150"), "TEATRO", LocalDateTime.now(), estrategiasDeValidacao));
    }

    @Test
    void limite_por_cpf_bloqueia() {
        Cupom cupom = cupomPercentual(20);
        cupom.registrarUso("CPF-A");
        assertThrows(IllegalArgumentException.class, () ->
                cupom.validarElegibilidade("CPF-A", new BigDecimal("150"), "TEATRO", LocalDateTime.now(), estrategiasDeValidacao));
    }

    @Test
    void percentual_invalido_falha_construcao() {
        assertThrows(IllegalArgumentException.class, () -> new Cupom(
                new CupomId("ID-X"), "X", TipoDesconto.PERCENTUAL,
                new BigDecimal("150"), BigDecimal.ZERO, 1, 1,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), null));
    }
}