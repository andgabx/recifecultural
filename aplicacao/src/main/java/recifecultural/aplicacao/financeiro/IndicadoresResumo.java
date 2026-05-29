package recifecultural.aplicacao.financeiro;

import java.math.BigDecimal;

public interface IndicadoresResumo {
    BigDecimal getTaxaOcupacao();
    BigDecimal getReceitaBruta();
    BigDecimal getReceitaLiquida();
    BigDecimal getTotalDespesas();
}
