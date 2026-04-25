package recifecultural.dominio.financeiro;

public interface IOrcamentoRepositorio {

    void salvar(OrcamentoPeriodo orcamento);

    OrcamentoPeriodo buscarPorId(OrcamentoId id);

    OrcamentoPeriodo buscarPorPeriodo(Periodo periodo);
}
