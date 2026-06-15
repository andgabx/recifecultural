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

    @Column(name = "id_evento")
    String idEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    StatusIngressoCatraca status;

    @Column(name = "horario_inicio_evento")
    LocalDateTime horarioInicioEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_ingresso")
    TipoIngresso tipoIngresso;

    @Column(name = "portao_acesso")
    String portaoAcesso;
}

interface IngressoCatracaJpaRepository extends JpaRepository<IngressoCatracaJpa, String> {
}
