package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/*
 * Padrão Strategy: define como calcular o resultado financeiro de um cancelamento
 * (reembolso, multa, motivo) e qual é o status final do patrocínio. Implementações
 * concretas representam cada política de cancelamento (por evento, por patrocinador).
 */
public interface EstrategiaCancelamentoPatrocinio {

    ResultadoCalculoCancelamento calcular(BigDecimal valorContribuicao,
                                          LocalDateTime dataEvento,
                                          LocalDateTime agora);

    StatusPatrocinio statusFinal();
}
