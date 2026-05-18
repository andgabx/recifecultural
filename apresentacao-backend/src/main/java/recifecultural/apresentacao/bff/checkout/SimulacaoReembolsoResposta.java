package recifecultural.apresentacao.bff.checkout;

import java.math.BigDecimal;

public record SimulacaoReembolsoResposta(
        BigDecimal valorReembolsado,
        boolean processado,
        String prazoProcessamento) {}
