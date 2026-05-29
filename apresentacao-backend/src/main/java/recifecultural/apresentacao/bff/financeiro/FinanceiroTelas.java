package recifecultural.apresentacao.bff.financeiro;

import java.math.BigDecimal;
import java.util.UUID;

public class FinanceiroTelas {
    public record RegistrarDespesaRequisicao(
            UUID orcamentoId,
            String descricao,
            BigDecimal valor,
            String categoria) {}
}
