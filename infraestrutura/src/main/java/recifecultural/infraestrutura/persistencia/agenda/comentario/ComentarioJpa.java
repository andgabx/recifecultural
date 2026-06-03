package recifecultural.infraestrutura.persistencia.agenda.comentario;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.agenda.comentario.StatusComentario;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comentario")
public class ComentarioJpa {
    @Id
    public UUID id;
    public UUID espectadorId;
    public UUID eventoId;
    public UUID comentarioPaiId;
    @Column(length = 500)
    public String texto;
    public Integer nota;
    @Enumerated(EnumType.STRING)
    public StatusComentario status;
    public LocalDateTime criadoEm;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comentario_curtida", joinColumns = @JoinColumn(name = "comentario_id"))
    @Column(name = "espectador_id")
    public Set<UUID> curtidas = new HashSet<>();
}

interface ComentarioJpaRepository extends JpaRepository<ComentarioJpa, UUID> {
    List<ComentarioJpa> findByEventoId(UUID eventoId);

    @Query("SELECT COUNT(c) > 0 FROM ComentarioJpa c WHERE c.espectadorId = :espectadorId AND c.eventoId = :eventoId AND c.nota IS NOT NULL AND c.status <> 'DELETADO'")
    boolean existeNotaPorEspectador(UUID espectadorId, UUID eventoId);
}
