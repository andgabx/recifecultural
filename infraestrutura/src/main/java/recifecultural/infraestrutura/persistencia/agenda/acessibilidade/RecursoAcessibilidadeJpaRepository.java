package recifecultural.infraestrutura.persistencia.agenda.acessibilidade;

import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.agenda.acessibilidade.StatusRecurso;

import java.util.List;
import java.util.UUID;

public interface RecursoAcessibilidadeJpaRepository extends JpaRepository<RecursoAcessibilidadeJpa, UUID> {
    List<RecursoAcessibilidadeJpa> findByApresentacaoId(UUID apresentacaoId);
    List<RecursoAcessibilidadeJpa> findByEventoIdAndStatus(UUID eventoId, StatusRecurso status);
    List<RecursoAcessibilidadeJpa> findByEventoId(UUID eventoId);
}
