package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;

public record ResultadoCalculoCancelamento(BigDecimal reembolso, BigDecimal multa, String motivo) {
}
