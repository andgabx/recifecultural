package recifecultural.infraestrutura.persistencia.catraca;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.catraca.ICatracaRepositorio;
import recifecultural.dominio.catraca.IngressoCatraca;

@Repository
public class CatracaRepositorioImpl implements ICatracaRepositorio {

    private final IngressoCatracaJpaRepository jpa;
    private final ModelMapper mapeador;

    public CatracaRepositorioImpl(IngressoCatracaJpaRepository jpa, ModelMapper mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public IngressoCatraca buscarPorId(String idIngresso) {
        return jpa.findById(idIngresso).map(i -> mapeador.map(i, IngressoCatraca.class)).orElse(null);
    }

    @Override
    public void salvar(IngressoCatraca ingresso) {
        jpa.save(mapeador.map(ingresso, IngressoCatracaJpa.class));
    }
}
