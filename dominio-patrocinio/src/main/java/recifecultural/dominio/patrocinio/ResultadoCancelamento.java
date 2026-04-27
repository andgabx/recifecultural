package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;

public class ResultadoCancelamento {

    private final BigDecimal valorReembolsado;
    private final BigDecimal multaAplicada;
    private final String motivo;

    public ResultadoCancelamento(BigDecimal valorReembolsado, BigDecimal multaAplicada, String motivo) {
        this.valorReembolsado = valorReembolsado;
        this.multaAplicada = multaAplicada;
        this.motivo = motivo;
    }

    public BigDecimal getValorReembolsado() { return valorReembolsado; }
    public BigDecimal getMultaAplicada() { return multaAplicada; }
    public String getMotivo() { return motivo; }
}
