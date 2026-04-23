package recifecultural.dominio.financeiro;

public final class ResultadoRegistroDespesa {

    private final Despesa despesa;
    private final boolean alertaOrcamento;

    public ResultadoRegistroDespesa(Despesa despesa, boolean alertaOrcamento) {
        this.despesa = despesa;
        this.alertaOrcamento = alertaOrcamento;
    }

    public Despesa getDespesa() { return despesa; }
    public boolean isAlertaOrcamento() { return alertaOrcamento; }
}
