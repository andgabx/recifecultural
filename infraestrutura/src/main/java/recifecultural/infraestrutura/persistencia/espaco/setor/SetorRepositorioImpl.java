package recifecultural.infraestrutura.persistencia.espaco.setor;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.setor.ISetorRepositorio;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.SetorId;
import recifecultural.infraestrutura.persistencia.jpa.JpaMapeador;

import java.util.List;
import java.util.Optional;

@Repository
public class SetorRepositorioImpl implements ISetorRepositorio {

    private final SetorJpaRepository jpa;
    private final JpaMapeador mapeador;

    public SetorRepositorioImpl(SetorJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Setor setor) {
        jpa.save(mapeador.map(setor, SetorJpa.class));
    }

    @Override
    public void atualizar(Setor setor) {
        jpa.save(mapeador.map(setor, SetorJpa.class));
    }

    @Override
    public Optional<Setor> obterPorId(SetorId id) {
        return jpa.findById(id.valor()).map(s -> mapeador.map(s, Setor.class));
    }

    @Override
    public List<Setor> listarPorEspaco(EspacoId espacoId) {
        return jpa.findByEspacoId(espacoId.valor())
                .stream().map(s -> mapeador.map(s, Setor.class)).toList();
    }
}
