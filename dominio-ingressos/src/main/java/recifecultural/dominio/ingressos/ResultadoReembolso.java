package recifecultural.dominio.ingressos;

import java.math.BigDecimal;

public final class ResultadoReembolso {

    private final BigDecimal valorReembolsado;
    private final boolean processado;
    private final String prazoProcessamento;

    public ResultadoReembolso(BigDecimal valorReembolsado, boolean processado, String prazoProcessamento) {
        this.valorReembolsado = valorReembolsado;
        this.processado = processado;
        this.prazoProcessamento = prazoProcessamento;
    }

    public BigDecimal getValorReembolsado() {
        return valorReembolsado;
    }

    public boolean isProcessado() {
        return processado;
    }

    public String getPrazoProcessamento() {
        return prazoProcessamento;
    }
}
