package recifecultural.infraestrutura.persistencia.artista.produtor;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.artista.produtor.StatusProdutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "historico_status_produtor")
public class HistoricoStatusProdutorJpa {
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
