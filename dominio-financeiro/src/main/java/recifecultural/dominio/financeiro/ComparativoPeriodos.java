package recifecultural.dominio.financeiro;

public final class ComparativoPeriodos {

    private final IndicadoresDesempenho periodoAnterior;
    private final IndicadoresDesempenho periodoAtual;

    public ComparativoPeriodos(IndicadoresDesempenho periodoAnterior, IndicadoresDesempenho periodoAtual) {
        this.periodoAnterior = periodoAnterior;
        this.periodoAtual = periodoAtual;
    }

    public IndicadoresDesempenho getPeriodoAnterior() { return periodoAnterior; }
    public IndicadoresDesempenho getPeriodoAtual() { return periodoAtual; }
}
