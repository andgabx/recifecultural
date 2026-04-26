package recifecultural.dominio.financeiro;

import java.math.BigDecimal;
import java.util.List;

public interface IDespesaRepositorio {

    void salvar(Despesa despesa);

    List<Despesa> buscarPorOrcamento(OrcamentoId id);

    BigDecimal somarPorOrcamento(OrcamentoId id);
}
