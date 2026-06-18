package recifecultural.aplicacao.inteligencia;

public class PrevisaoNoShowResposta {
    private String ingressoId;
    private double probabilidadeFalta;
    private boolean alertaAltoRisco;

    public PrevisaoNoShowResposta(String ingressoId, double probabilidadeFalta, boolean alertaAltoRisco) {
        this.ingressoId = ingressoId;
        this.probabilidadeFalta = probabilidadeFalta;
        this.alertaAltoRisco = alertaAltoRisco;
    }

    // Getters
    public String getIngressoId() { return ingressoId; }
    public double getProbabilidadeFalta() { return probabilidadeFalta; }
    public boolean isAlertaAltoRisco() { return alertaAltoRisco; }
}