package recifecultural.infraestrutura.persistencia.artista.produtor;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.artista.produtor.HistoricoStatusProdutor;
import recifecultural.dominio.artista.produtor.IHistoricoStatusProdutorRepositorio;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.List;

@Repository
public class HistoricoStatusProdutorRepositorioImpl implements IHistoricoStatusProdutorRepositorio {

    private final HistoricoStatusProdutorJpaRepository jpa;
    private final ProdutorMapeador mapeador;

    public HistoricoStatusProdutorRepositorioImpl(HistoricoStatusProdutorJpaRepository jpa, ProdutorMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(HistoricoStatusProdutor historico) {
        jpa.save(mapeador.toJpa(historico));
    }

    @Override
    public List<HistoricoStatusProdutor> listarPorProdutor(ProdutorId produtorId) {
        return jpa.findByProdutorId(produtorId.valor()).stream()
                .map(mapeador::toDomain)
                .toList();
    }
}
