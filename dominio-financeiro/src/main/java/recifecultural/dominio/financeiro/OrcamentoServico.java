package recifecultural.dominio.financeiro;

import java.math.BigDecimal;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class OrcamentoServico {

    private final IOrcamentoRepositorio orcamentoRepositorio;
    private final IDespesaRepositorio despesaRepositorio;

    public OrcamentoServico(IOrcamentoRepositorio orcamentoRepositorio,
                             IDespesaRepositorio despesaRepositorio) {
        notNull(orcamentoRepositorio, "Repositório de orçamento é obrigatório.");
        notNull(despesaRepositorio, "Repositório de despesas é obrigatório.");
        this.orcamentoRepositorio = orcamentoRepositorio;
        this.despesaRepositorio = despesaRepositorio;
    }

    public OrcamentoPeriodo criar(Periodo periodo, BigDecimal valorTotal) {
        notNull(periodo, "Período é obrigatório.");
        notNull(valorTotal, "Valor total é obrigatório.");
        OrcamentoPeriodo orcamento = new OrcamentoPeriodo(OrcamentoId.novo(), periodo, valorTotal);
        orcamentoRepositorio.salvar(orcamento);
        return orcamento;
    }

    public void reduzir(OrcamentoId id, BigDecimal novoValor) {
        notNull(id, "ID do orçamento é obrigatório.");
        notNull(novoValor, "Novo valor é obrigatório.");
        OrcamentoPeriodo orcamento = buscarOuLancar(id);
        isTrue(orcamento.getStatus() == StatusOrcamento.ABERTO, "Orçamento encerrado não pode ser reduzido.");
        BigDecimal totalDespesas = despesaRepositorio.somarPorOrcamento(id);
        orcamento.reduzir(novoValor, totalDespesas);
        orcamentoRepositorio.salvar(orcamento);
    }

    public void encerrar(OrcamentoId id) {
        notNull(id, "ID do orçamento é obrigatório.");
        OrcamentoPeriodo orcamento = buscarOuLancar(id);
        orcamento.encerrar();
        orcamentoRepositorio.salvar(orcamento);
    }

    private OrcamentoPeriodo buscarOuLancar(OrcamentoId id) {
        OrcamentoPeriodo orcamento = orcamentoRepositorio.buscarPorId(id);
        if (orcamento == null) throw new IllegalArgumentException("Orçamento não encontrado: " + id.valor());
        return orcamento;
    }
}
