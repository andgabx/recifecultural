package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;
import recifecultural.dominio.agenda.comentario.Nota;
import recifecultural.dominio.agenda.comentario.StatusComentario;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "comentario")
class ComentarioJpa {
    @Id
    UUID id;
    UUID espectadorId;
    UUID eventoId;
    UUID comentarioPaiId;
    @Column(length = 500)
    String texto;
    Integer nota;
    @Enumerated(EnumType.STRING)
    StatusComentario status;
    LocalDateTime criadoEm;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "comentario_curtida", joinColumns = @JoinColumn(name = "comentario_id"))
    @Column(name = "espectador_id")
    Set<UUID> curtidas = new HashSet<>();
}

interface ComentarioJpaRepository extends JpaRepository<ComentarioJpa, UUID> {
    List<ComentarioJpa> findByEventoId(UUID eventoId);

    @Query("SELECT COUNT(c) > 0 FROM ComentarioJpa c WHERE c.espectadorId = :espectadorId AND c.eventoId = :eventoId AND c.nota IS NOT NULL AND c.status <> 'DELETADO'")
    boolean existeNotaPorEspectador(UUID espectadorId, UUID eventoId);
}

@Repository
class ComentarioRepositorioImpl implements ComentarioRepositorio {

    private final ComentarioJpaRepository jpa;
    private final JpaMapeador mapeador;

    ComentarioRepositorioImpl(ComentarioJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Comentario comentario) {
        jpa.save(mapeador.map(comentario, ComentarioJpa.class));
    }

    @Override
    public Optional<Comentario> obter(UUID id) {
        return jpa.findById(id).map(c -> mapeador.map(c, Comentario.class));
    }

    @Override
    public void atualizar(Comentario comentario) {
        jpa.save(mapeador.map(comentario, ComentarioJpa.class));
    }

    @Override
    public void deletar(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Comentario> listarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .map(c -> mapeador.map(c, Comentario.class))
                .toList();
    }

    @Override
    public boolean existeNotaPorEspectador(UUID espectadorId, UUID eventoId) {
        return jpa.existeNotaPorEspectador(espectadorId, eventoId);
    }
}
