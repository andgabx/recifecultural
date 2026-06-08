package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface EstrategiaCancelamentoPatrocinio {

    ResultadoCalculoCancelamento calcular(BigDecimal valorContribuicao,
                                          LocalDateTime dataEvento,
                                          LocalDateTime agora);

    StatusPatrocinio statusFinal();
}
