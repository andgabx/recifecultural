package recifecultural.infraestrutura.persistencia.financeiro;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.financeiro.IOrcamentoRepositorio;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.dominio.financeiro.OrcamentoPeriodo;
import recifecultural.dominio.financeiro.Periodo;
import recifecultural.infraestrutura.persistencia.jpa.JpaMapeador;

@Repository
public class OrcamentoRepositorioImpl implements IOrcamentoRepositorio {

    private final OrcamentoPeriodoJpaRepository jpa;
    private final JpaMapeador mapeador;

    public OrcamentoRepositorioImpl(OrcamentoPeriodoJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(OrcamentoPeriodo orcamento) {
        jpa.save(mapeador.map(orcamento, OrcamentoPeriodoJpa.class));
    }

    @Override
    public OrcamentoPeriodo buscarPorId(OrcamentoId id) {
        return jpa.findById(id.valor()).map(o -> mapeador.map(o, OrcamentoPeriodo.class)).orElse(null);
    }

    @Override
    public OrcamentoPeriodo buscarPorPeriodo(Periodo periodo) {
        var jpaObj = jpa.findByPeriodo(periodo.getDataInicio(), periodo.getDataFim());
        return jpaObj != null ? mapeador.map(jpaObj, OrcamentoPeriodo.class) : null;
    }
}
