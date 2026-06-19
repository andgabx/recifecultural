package recifecultural.aplicacao.inteligencia;

import java.util.UUID;

public class PrevisaoNoShowResposta {
    private UUID eventoId;
    private double probabilidadeNoShow;
    private boolean alertaAltoRisco;

    public PrevisaoNoShowResposta(UUID eventoId, double probabilidadeNoShow, boolean alertaAltoRisco) {
        this.eventoId = eventoId;
        this.probabilidadeNoShow = probabilidadeNoShow;
        this.alertaAltoRisco = alertaAltoRisco;
    }

    // Getters
    public UUID getEventoId() { return eventoId; }
    public double getProbabilidadeNoShow() { return probabilidadeNoShow; }
    public boolean isAlertaAltoRisco() { return alertaAltoRisco; }
}