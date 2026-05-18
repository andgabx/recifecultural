package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EstrategiaCancelamentoPorEvento implements EstrategiaCancelamentoPatrocinio {

    private static final BigDecimal PERCENTUAL_PARCIAL = new BigDecimal("0.50");

    @Override
    public ResultadoCalculoCancelamento calcular(BigDecimal valorContribuicao,
                                                 LocalDateTime dataEvento,
                                                 LocalDateTime agora) {
        long diasRestantes = ChronoUnit.DAYS.between(agora.toLocalDate(), dataEvento.toLocalDate());

        if (diasRestantes > 7) {
            return new ResultadoCalculoCancelamento(
                    valorContribuicao, BigDecimal.ZERO,
                    "Cancelamento pelo evento com mais de 7 dias de antecedência. Reembolso integral.");
        }
        if (diasRestantes >= 2) {
            return new ResultadoCalculoCancelamento(
                    valorContribuicao.multiply(PERCENTUAL_PARCIAL), BigDecimal.ZERO,
                    "Cancelamento pelo evento entre 2 e 7 dias de antecedência. Reembolso de 50%.");
        }
        return new ResultadoCalculoCancelamento(
                BigDecimal.ZERO, BigDecimal.ZERO,
                "Cancelamento pelo evento com menos de 2 dias de antecedência. Sem reembolso.");
    }

    @Override
    public StatusPatrocinio statusFinal() {
        return StatusPatrocinio.CANCELADO_EVENTO;
    }
}
