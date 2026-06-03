package recifecultural.infraestrutura.persistencia.catraca;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.catraca.StatusIngressoCatraca;
import recifecultural.dominio.catraca.TipoIngresso;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingresso_catraca")
public class IngressoCatracaJpa {
    @Id
    String id;
    String idEvento;
    @Enumerated(EnumType.STRING)
    StatusIngressoCatraca status;
    LocalDateTime horarioInicioEvento;
    @Enumerated(EnumType.STRING)
    TipoIngresso tipoIngresso;
    String portaoAcesso;
}

public interface IngressoCatracaJpaRepository extends JpaRepository<IngressoCatracaJpa, String> {
}
