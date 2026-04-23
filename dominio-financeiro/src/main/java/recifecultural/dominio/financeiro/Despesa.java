package recifecultural.dominio.financeiro;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Despesa {

    private final DespesaId id;
    private final OrcamentoId orcamentoId;
    private final String descricao;
    private final BigDecimal valor;
    private final CategoriaDespesa categoria;
    private final LocalDateTime dataRegistro;

    public Despesa(DespesaId id,
                   OrcamentoId orcamentoId,
                   String descricao,
                   BigDecimal valor,
                   CategoriaDespesa categoria) {
        notNull(id, "O id da despesa não pode ser nulo.");
        notNull(orcamentoId, "O id do orçamento não pode ser nulo.");
        notBlank(descricao, "A descrição da despesa não pode ser vazia.");
        notNull(valor, "O valor da despesa não pode ser nulo.");
        isTrue(valor.compareTo(BigDecimal.ZERO) > 0, "O valor da despesa deve ser maior que zero.");
        notNull(categoria, "A categoria da despesa não pode ser nula.");

        this.id = id;
        this.orcamentoId = orcamentoId;
        this.descricao = descricao;
        this.valor = valor;
        this.categoria = categoria;
        this.dataRegistro = LocalDateTime.now();
    }

    public DespesaId getId() { return id; }
    public OrcamentoId getOrcamentoId() { return orcamentoId; }
    public String getDescricao() { return descricao; }
    public BigDecimal getValor() { return valor; }
    public CategoriaDespesa getCategoria() { return categoria; }
    public LocalDateTime getDataRegistro() { return dataRegistro; }
}
