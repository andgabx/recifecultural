package recifecultural.dominio.financeiro;

import java.math.BigDecimal;

public final class IndicadoresDesempenho {

    private final BigDecimal taxaOcupacao;
    private final BigDecimal receitaBruta;
    private final BigDecimal receitaLiquida;
    private final BigDecimal totalDespesas;
    private final BigDecimal crescimentoPublico;

    public IndicadoresDesempenho(BigDecimal taxaOcupacao,
                                  BigDecimal receitaBruta,
                                  BigDecimal receitaLiquida,
                                  BigDecimal totalDespesas,
                                  BigDecimal crescimentoPublico) {
        this.taxaOcupacao = taxaOcupacao;
        this.receitaBruta = receitaBruta;
        this.receitaLiquida = receitaLiquida;
        this.totalDespesas = totalDespesas;
        this.crescimentoPublico = crescimentoPublico;
    }

    public BigDecimal getTaxaOcupacao() { return taxaOcupacao; }
    public BigDecimal getReceitaBruta() { return receitaBruta; }
    public BigDecimal getReceitaLiquida() { return receitaLiquida; }
    public BigDecimal getTotalDespesas() { return totalDespesas; }
    public BigDecimal getCrescimentoPublico() { return crescimentoPublico; }
}
