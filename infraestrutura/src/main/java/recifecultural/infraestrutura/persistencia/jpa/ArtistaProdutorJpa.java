package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.artista.artista.ArtistaRepositorioAplicacao;
import recifecultural.aplicacao.artista.artista.ArtistaResumo;
import recifecultural.aplicacao.artista.produtor.ProdutorRepositorioAplicacao;
import recifecultural.aplicacao.artista.produtor.ProdutorResumo;
import recifecultural.dominio.artista.artista.Artista;
import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.artista.IArtistaRepositorio;
import recifecultural.dominio.artista.artista.StatusArtista;
import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.IProdutorRepositorio;
import recifecultural.dominio.artista.produtor.Produtor;
import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.dominio.artista.produtor.StatusProdutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "artista")
class ArtistaJpa {
    @Id
    UUID id;
    UUID produtorId;
    String nome;
    @Enumerated(EnumType.STRING)
    StatusArtista status;
}

@Entity
@Table(name = "produtor")
class ProdutorJpa {
    @Id
    UUID id;
    String nomeFantasia;
    String cnpj;
    String email;
    String telefone;
    @Enumerated(EnumType.STRING)
    StatusProdutor status;
}

interface ArtistaJpaRepository extends JpaRepository<ArtistaJpa, UUID> {
    List<ArtistaJpa> findByProdutorId(UUID produtorId);

    @Query("SELECT COUNT(a) > 0 FROM ArtistaJpa a WHERE a.nome = :nome AND a.produtorId = :produtorId")
    boolean existsByNomeAndProdutorId(String nome, UUID produtorId);
}

interface ProdutorJpaRepository extends JpaRepository<ProdutorJpa, UUID> {
    boolean existsByCnpj(String cnpj);
}

@Repository
class ArtistaRepositorioImpl implements IArtistaRepositorio, ArtistaRepositorioAplicacao {

    private final ArtistaJpaRepository jpa;
    private final JpaMapeador mapeador;

    ArtistaRepositorioImpl(ArtistaJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Artista artista) {
        jpa.save(mapeador.map(artista, ArtistaJpa.class));
    }

    @Override
    public void atualizar(Artista artista) {
        jpa.save(mapeador.map(artista, ArtistaJpa.class));
    }

    @Override
    public Optional<Artista> obterPorId(ArtistaId id) {
        return jpa.findById(id.valor()).map(a -> mapeador.map(a, Artista.class));
    }

    @Override
    public List<Artista> listarPorProdutor(ProdutorId produtorId) {
        return jpa.findByProdutorId(produtorId.valor())
                .stream().map(a -> mapeador.map(a, Artista.class)).toList();
    }

    @Override
    public boolean existePorNomeEProdutor(String nome, ProdutorId produtorId) {
        return jpa.existsByNomeAndProdutorId(nome, produtorId.valor());
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

    record ArtistaResumoJpa(String id, String produtorId, String nome, String status) implements ArtistaResumo {
        public String getId() { return id; }
        public String getProdutorId() { return produtorId; }
        public String getNome() { return nome; }
        public String getStatus() { return status; }
    }
}

@Repository
class ProdutorRepositorioImpl implements IProdutorRepositorio, ProdutorRepositorioAplicacao {

    private final ProdutorJpaRepository jpa;
    private final JpaMapeador mapeador;

    ProdutorRepositorioImpl(ProdutorJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Produtor produtor) {
        jpa.save(mapeador.map(produtor, ProdutorJpa.class));
    }

    @Override
    public void atualizar(Produtor produtor) {
        jpa.save(mapeador.map(produtor, ProdutorJpa.class));
    }

    @Override
    public Optional<Produtor> obterPorId(ProdutorId id) {
        return jpa.findById(id.valor()).map(p -> mapeador.map(p, Produtor.class));
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
