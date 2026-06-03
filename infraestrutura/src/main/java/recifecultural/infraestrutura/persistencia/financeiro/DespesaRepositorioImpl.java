package recifecultural.infraestrutura.persistencia.financeiro;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.financeiro.Despesa;
import recifecultural.dominio.financeiro.IDespesaRepositorio;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.infraestrutura.persistencia.jpa.JpaMapeador;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class DespesaRepositorioImpl implements IDespesaRepositorio {

    private final DespesaJpaRepository jpa;
    private final JpaMapeador mapeador;

    public DespesaRepositorioImpl(DespesaJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Despesa despesa) {
        jpa.save(mapeador.map(despesa, DespesaJpa.class));
    }

    @Override
    public List<Despesa> buscarPorOrcamento(OrcamentoId id) {
        return jpa.findByOrcamentoId(id.valor())
                .stream().map(d -> mapeador.map(d, Despesa.class)).toList();
    }

    @Override
    public BigDecimal somarPorOrcamento(OrcamentoId id) {
        BigDecimal soma = jpa.somarPorOrcamento(id.valor());
        return soma != null ? soma : BigDecimal.ZERO;
    }
}
