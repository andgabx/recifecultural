package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class EstrategiaCancelamentoPorPatrocinador implements EstrategiaCancelamentoPatrocinio {

    private static final BigDecimal PERCENTUAL_MULTA = new BigDecimal("0.20");
    private static final BigDecimal PERCENTUAL_REEMBOLSO_COM_MULTA = new BigDecimal("0.80");

    @Override
    public ResultadoCalculoCancelamento calcular(BigDecimal valorContribuicao,
                                                 LocalDateTime dataEvento,
                                                 LocalDateTime agora) {
        long diasRestantes = ChronoUnit.DAYS.between(agora.toLocalDate(), dataEvento.toLocalDate());

        if (diasRestantes > 15) {
            return new ResultadoCalculoCancelamento(
                    valorContribuicao, BigDecimal.ZERO,
                    "Cancelamento pelo patrocinador com mais de 15 dias de antecedência. Sem penalidade.");
        }
        return new ResultadoCalculoCancelamento(
                valorContribuicao.multiply(PERCENTUAL_REEMBOLSO_COM_MULTA),
                valorContribuicao.multiply(PERCENTUAL_MULTA),
                "Cancelamento pelo patrocinador com até 15 dias de antecedência. Multa de 20% aplicada.");
    }

    @Override
    public StatusPatrocinio statusFinal() {
        return StatusPatrocinio.CANCELADO_PATROCINADOR;
    }
}
