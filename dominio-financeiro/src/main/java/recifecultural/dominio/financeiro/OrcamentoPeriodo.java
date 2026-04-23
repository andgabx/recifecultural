package recifecultural.dominio.financeiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class OrcamentoPeriodo {

    private final OrcamentoId id;
    private final Periodo periodo;
    private BigDecimal valorTotal;
    private StatusOrcamento status;

    public OrcamentoPeriodo(OrcamentoId id, Periodo periodo, BigDecimal valorTotal) {
        notNull(id, "O id do orçamento não pode ser nulo.");
        notNull(periodo, "O período não pode ser nulo.");
        notNull(valorTotal, "O valor total não pode ser nulo.");
        isTrue(valorTotal.compareTo(BigDecimal.ZERO) > 0, "O valor total deve ser maior que zero.");

        this.id = id;
        this.periodo = periodo;
        this.valorTotal = valorTotal;
        this.status = StatusOrcamento.ABERTO;
    }

    public void reduzir(BigDecimal novoValor, BigDecimal totalDespesasRegistradas) {
        notNull(novoValor, "O novo valor não pode ser nulo.");
        notNull(totalDespesasRegistradas, "O total de despesas não pode ser nulo.");
        isTrue(novoValor.compareTo(totalDespesasRegistradas) >= 0,
                "Orçamento não pode ser menor que as despesas já registradas.");
        this.valorTotal = novoValor;
    }

    public void encerrar() {
        this.status = StatusOrcamento.ENCERRADO;
    }

    public OrcamentoId getId() { return id; }
    public Periodo getPeriodo() { return periodo; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public StatusOrcamento getStatus() { return status; }
}
