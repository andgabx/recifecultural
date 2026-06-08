package recifecultural.infraestrutura.persistencia.espaco.suporte;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.suporte.StatusChamado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chamado_suporte")
public class ChamadoSuporteJpa {
    @Id
    UUID id;
    UUID assentoId;
    @Column(length = 1000)
    String descricao;
    @Enumerated(EnumType.STRING)
    MotivoIndisponibilidadeAssento motivo;
    @Enumerated(EnumType.STRING)
    StatusChamado status;
    LocalDateTime dataAbertura;
}

interface ChamadoSuporteJpaRepository extends JpaRepository<ChamadoSuporteJpa, UUID> {
    @Query("SELECT c FROM ChamadoSuporteJpa c WHERE c.status IN ('ABERTO','EM_ANDAMENTO') AND c.dataAbertura < :limite")
    List<ChamadoSuporteJpa> findAbertosAntesDe(LocalDateTime limite);
}
