package recifecultural.aplicacao.inteligencia;

public class PrevisaoNoShowRequisicao {
    private String ingressoId;
    private int antecedenciaCompraDias;
    private String previsaoClima = "ensolarado";

    // Getters e Setters
    public String getIngressoId() { return ingressoId; }
    public void setIngressoId(String ingressoId) { this.ingressoId = ingressoId; }
    public int getAntecedenciaCompraDias() { return antecedenciaCompraDias; }
    public void setAntecedenciaCompraDias(int antecedenciaCompraDias) { this.antecedenciaCompraDias = antecedenciaCompraDias; }
    public String getPrevisaoClima() { return previsaoClima; }
    public void setPrevisaoClima(String previsaoClima) { this.previsaoClima = previsaoClima; }
}