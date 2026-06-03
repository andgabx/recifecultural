package recifecultural.infraestrutura.persistencia.artista.produtor;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.artista.produtor.StatusProdutor;

import java.util.UUID;

@Entity
@Table(name = "produtor")
public class ProdutorJpa {
    @Id
    UUID id;
    String nomeFantasia;
    String cnpj;
    String email;
    String telefone;
    @Enumerated(EnumType.STRING)
    StatusProdutor status;
}

public interface ProdutorJpaRepository extends JpaRepository<ProdutorJpa, UUID> {
    boolean existsByCnpj(String cnpj);
}
