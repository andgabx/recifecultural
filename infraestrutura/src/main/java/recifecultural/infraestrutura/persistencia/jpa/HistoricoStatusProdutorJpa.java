package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.artista.produtor.HistoricoStatusProdutor;
import recifecultural.dominio.artista.produtor.IHistoricoStatusProdutorRepositorio;
import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.dominio.artista.produtor.StatusProdutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "historico_status_produtor")
class HistoricoStatusProdutorJpa {
    @Id
    UUID id;
    UUID produtorId;
    @Enumerated(EnumType.STRING)
    StatusProdutor statusAnterior;
    @Enumerated(EnumType.STRING)
    StatusProdutor statusNovo;
    String responsavel;
    String motivo;
    LocalDateTime dataAlteracao;
}

interface HistoricoStatusProdutorJpaRepository extends JpaRepository<HistoricoStatusProdutorJpa, UUID> {
    List<HistoricoStatusProdutorJpa> findByProdutorId(UUID produtorId);
}

@Repository
class HistoricoStatusProdutorRepositorioImpl implements IHistoricoStatusProdutorRepositorio {

    private final HistoricoStatusProdutorJpaRepository jpa;
    private final JpaMapeador mapeador;

    HistoricoStatusProdutorRepositorioImpl(HistoricoStatusProdutorJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(HistoricoStatusProdutor historico) {
        jpa.save(mapeador.map(historico, HistoricoStatusProdutorJpa.class));
    }

    @Override
    public List<HistoricoStatusProdutor> listarPorProdutor(ProdutorId produtorId) {
        return jpa.findByProdutorId(produtorId.valor()).stream()
                .map(h -> mapeador.map(h, HistoricoStatusProdutor.class))
                .toList();
    }
}
