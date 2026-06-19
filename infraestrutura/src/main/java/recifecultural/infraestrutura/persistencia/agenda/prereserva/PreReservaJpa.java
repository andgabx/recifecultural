package recifecultural.infraestrutura.persistencia.agenda.prereserva;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import recifecultural.dominio.agenda.prereserva.StatusPreReserva;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "pre_reserva")
public class PreReservaJpa {
    @Id
    public UUID id;
    public UUID assentoId;
    public UUID setorId;
    public UUID usuarioId;
    public UUID eventoId;
    public LocalDateTime criadaEm;
    public LocalDateTime expiraEm;
    @Enumerated(EnumType.STRING)
    public StatusPreReserva status;
    public int versao;
}

interface PreReservaJpaRepository extends JpaRepository<PreReservaJpa, UUID> {
    @Query("SELECT p FROM PreReservaJpa p WHERE p.assentoId = :assentoId AND p.eventoId = :eventoId AND p.status = 'ATIVA'")
    List<PreReservaJpa> findAtivasPorAssentoEEvento(@Param("assentoId") UUID assentoId, @Param("eventoId") UUID eventoId);

    @Query("SELECT p FROM PreReservaJpa p WHERE p.eventoId = :eventoId AND p.status = 'ATIVA'")
    List<PreReservaJpa> findAtivasPorEvento(UUID eventoId);

    @Query("SELECT p FROM PreReservaJpa p WHERE p.status = 'ATIVA' AND p.expiraEm <= :agora")
    List<PreReservaJpa> findAtivasExpiradas(LocalDateTime agora);
}
