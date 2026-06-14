package recifecultural.infraestrutura.persistencia.agenda.acessibilidade;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.agenda.acessibilidade.StatusRecurso;

import java.util.List;
import java.util.UUID;

public interface RecursoAcessibilidadeJpaRepository extends JpaRepository<RecursoAcessibilidadeJpa, UUID> {
    List<RecursoAcessibilidadeJpa> findByApresentacaoId(UUID apresentacaoId);
    List<RecursoAcessibilidadeJpa> findByEventoIdAndStatus(UUID eventoId, StatusRecurso status);
    List<RecursoAcessibilidadeJpa> findByEventoId(UUID eventoId);

    @Query("""
            SELECT r
            FROM RecursoAcessibilidadeJpa r
            WHERE r.eventoId = :eventoId
            ORDER BY
                CASE
                    WHEN r.status = recifecultural.dominio.agenda.acessibilidade.StatusRecurso.CONFIRMADO THEN 0
                    WHEN r.status = recifecultural.dominio.agenda.acessibilidade.StatusRecurso.REMOVIDO THEN 1
                    ELSE 2
                END,
                r.tipo
            """)
    List<RecursoAcessibilidadeJpa> findRecursosOrdenados(UUID eventoId, Pageable pageable);
}
