package recifecultural.infraestrutura.persistencia.cupom;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.cupom.Cupom;
import recifecultural.dominio.cupom.CupomId;
import recifecultural.dominio.cupom.ICupomRepositorio;

import java.util.List;

@Repository
public class CupomRepositorioImpl implements ICupomRepositorio {

    private final CupomJpaRepository jpa;
    private final ModelMapper mapeador;

    public CupomRepositorioImpl(CupomJpaRepository jpa, ModelMapper mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public Cupom buscarPorCodigo(String codigo) {
        var jpaObj = jpa.findByCodigo(codigo);
        return jpaObj != null ? mapeador.map(jpaObj, Cupom.class) : null;
    }

    @Override
    public Cupom buscarPorId(CupomId id) {
        return jpa.findById(id.getValor().toString())
                .map(c -> mapeador.map(c, Cupom.class))
                .orElse(null);
    }

    @Override
    public List<Cupom> listarTodos() {
        return jpa.findAll().stream()
                .map(c -> mapeador.map(c, Cupom.class))
                .toList();
    }

    @Override
    public void salvar(Cupom cupom) {
        jpa.save(mapeador.map(cupom, CupomJpa.class));
    }

    @Override
    public void deletar(CupomId id) {
        jpa.deleteById(id.getValor().toString());
    }
}
