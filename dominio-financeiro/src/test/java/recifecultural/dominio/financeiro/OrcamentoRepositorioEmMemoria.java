package recifecultural.dominio.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrcamentoRepositorioEmMemoria implements IOrcamentoRepositorio {

    private final Map<OrcamentoId, OrcamentoPeriodo> orcamentos = new HashMap<>();

    @Override
    public void salvar(OrcamentoPeriodo orcamento) {
        orcamentos.put(orcamento.getId(), orcamento);
    }

    @Override
    public OrcamentoPeriodo buscarPorId(OrcamentoId id) {
        return orcamentos.get(id);
    }

    @Override
    public OrcamentoPeriodo buscarPorPeriodo(Periodo periodo) {
        return orcamentos.values().stream()
                .filter(o -> o.getPeriodo().getDataInicio().equals(periodo.getDataInicio())
                        && o.getPeriodo().getDataFim().equals(periodo.getDataFim()))
                .findFirst()
                .orElse(null);
    }
}
