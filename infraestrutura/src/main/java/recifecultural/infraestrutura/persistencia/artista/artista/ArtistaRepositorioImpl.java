package recifecultural.infraestrutura.persistencia.artista.artista;

import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.artista.artista.ArtistaRepositorioAplicacao;
import recifecultural.aplicacao.artista.artista.ArtistaResumo;
import recifecultural.dominio.artista.artista.Artista;
import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.artista.IArtistaRepositorio;
import recifecultural.dominio.artista.artista.Iterador;
import recifecultural.dominio.artista.artista.RiderTecnico;
import recifecultural.dominio.artista.artista.StatusArtista;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Repository
public class ArtistaRepositorioImpl implements IArtistaRepositorio, ArtistaRepositorioAplicacao {

    private final ArtistaJpaRepository jpa;
    private final ArtistaMapeador mapeador;

    public ArtistaRepositorioImpl(ArtistaJpaRepository jpa, ArtistaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Artista artista) {
        jpa.save(mapeador.toJpa(artista));
    }

    @Override
    public void atualizar(Artista artista) {
        jpa.save(mapeador.toJpa(artista));
    }

    @Override
    public Optional<Artista> obterPorId(ArtistaId id) {
        return jpa.findById(id.valor()).map(mapeador::toDomain);
    }

    @Override
    public List<Artista> listarPorProdutor(ProdutorId produtorId) {
        return jpa.findByProdutorId(produtorId.valor())
                .stream().map(mapeador::toDomain).toList();
    }

    @Override
    public boolean existePorNomeEProdutor(String nome, ProdutorId produtorId) {
        return jpa.existsByNomeAndProdutorId(nome, produtorId.valor());
    }

    @Override
    public boolean existeAtivoPorProdutor(ProdutorId produtorId) {
        return jpa.existsByProdutorIdAndStatus(produtorId.valor(), StatusArtista.ATIVO);
    }

    @Override
    public Iterador<Artista> iterarTodos() {
        return new IteradorPaginadoArtistas(jpa, mapeador);
    }

    @Override
    public List<ArtistaResumo> pesquisarResumos() {
        return jpa.findAll().stream()
                .<ArtistaResumo>map(a -> new ArtistaResumoJpa(
                        a.id.toString(),
                        a.produtorId != null ? a.produtorId.toString() : null,
                        a.nome,
                        a.status != null ? a.status.name() : null))
                .toList();
    }

    @Override
    public List<ArtistaResumo> pesquisarResumosPorProdutor(ProdutorId produtorId) {
        return jpa.findByProdutorId(produtorId.valor()).stream()
                .<ArtistaResumo>map(a -> new ArtistaResumoJpa(
                        a.id.toString(),
                        a.produtorId != null ? a.produtorId.toString() : null,
                        a.nome,
                        a.status != null ? a.status.name() : null))
                .toList();
    }

    record ArtistaResumoJpa(String id, String produtorId, String nome, String status) implements ArtistaResumo {
        public String getId() { return id; }
        public String getProdutorId() { return produtorId; }
        public String getNome() { return nome; }
        public String getStatus() { return status; }
    }
}
