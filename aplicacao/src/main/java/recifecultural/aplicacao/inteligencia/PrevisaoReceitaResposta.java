package recifecultural.aplicacao.inteligencia;

public class PrevisaoReceitaResposta {
    private double investimentoTotal;
    private double receitaEstimada;

    public PrevisaoReceitaResposta(double investimentoTotal, double receitaEstimada) {
        this.investimentoTotal = investimentoTotal;
        this.receitaEstimada = receitaEstimada;
    }

    // Getters
    public double getInvestimentoTotal() { return investimentoTotal; }
    public double getReceitaEstimada() { return receitaEstimada; }
}