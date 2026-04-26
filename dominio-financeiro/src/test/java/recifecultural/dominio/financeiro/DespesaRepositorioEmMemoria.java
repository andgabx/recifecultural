package recifecultural.dominio.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DespesaRepositorioEmMemoria implements IDespesaRepositorio {

    private final Map<DespesaId, Despesa> despesas = new HashMap<>();

    @Override
    public void salvar(Despesa despesa) {
        despesas.put(despesa.getId(), despesa);
    }

    @Override
    public List<Despesa> buscarPorOrcamento(OrcamentoId id) {
        return despesas.values().stream()
                .filter(d -> d.getOrcamentoId().equals(id))
                .toList();
    }

    @Override
    public BigDecimal somarPorOrcamento(OrcamentoId id) {
        return despesas.values().stream()
                .filter(d -> d.getOrcamentoId().equals(id))
                .map(Despesa::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
