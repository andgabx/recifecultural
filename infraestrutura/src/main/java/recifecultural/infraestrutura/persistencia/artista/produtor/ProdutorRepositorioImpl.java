package recifecultural.infraestrutura.persistencia.artista.produtor;

import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.artista.produtor.ProdutorRepositorioAplicacao;
import recifecultural.aplicacao.artista.produtor.ProdutorResumo;
import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.IProdutorRepositorio;
import recifecultural.dominio.artista.produtor.Produtor;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.List;
import java.util.Optional;

@Repository
public class ProdutorRepositorioImpl implements IProdutorRepositorio, ProdutorRepositorioAplicacao {

    private final ProdutorJpaRepository jpa;
    private final ProdutorMapeador mapeador;

    public ProdutorRepositorioImpl(ProdutorJpaRepository jpa, ProdutorMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Produtor produtor) {
        jpa.save(mapeador.toJpa(produtor));
    }

    @Override
    public void atualizar(Produtor produtor) {
        jpa.save(mapeador.toJpa(produtor));
    }

    @Override
    public Optional<Produtor> obterPorId(ProdutorId id) {
        return jpa.findById(id.valor()).map(mapeador::toDomain);
    }

    @Override
    public boolean existePorCnpj(Cnpj cnpj) {
        return jpa.existsByCnpj(cnpj.valor());
    }

    @Override
    public List<ProdutorResumo> pesquisarResumos() {
        return jpa.findAll().stream()
                .<ProdutorResumo>map(p -> new ProdutorResumoJpa(
                        p.id.toString(), p.nomeFantasia,
                        p.status != null ? p.status.name() : null))
                .toList();
    }

    record ProdutorResumoJpa(String id, String nomeFantasia, String status) implements ProdutorResumo {
        public String getId() { return id; }
        public String getNomeFantasia() { return nomeFantasia; }
        public String getStatus() { return status; }
    }
}
