package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.agenda.acessibilidade.IRecursoAcessibilidadeRepositorio;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidade;
import recifecultural.dominio.agenda.acessibilidade.StatusRecurso;
import recifecultural.dominio.agenda.acessibilidade.TipoRecursoAcessibilidade;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "recurso_acessibilidade")
class RecursoAcessibilidadeJpa {
    @Id
    UUID id;
    UUID apresentacaoId;
    UUID eventoId;
    @Enumerated(EnumType.STRING)
    TipoRecursoAcessibilidade tipo;
    @Enumerated(EnumType.STRING)
    StatusRecurso status;
    String justificativaRemocao;
}

interface RecursoAcessibilidadeJpaRepository extends JpaRepository<RecursoAcessibilidadeJpa, UUID> {
    List<RecursoAcessibilidadeJpa> findByApresentacaoId(UUID apresentacaoId);
    List<RecursoAcessibilidadeJpa> findByEventoIdAndStatus(UUID eventoId, StatusRecurso status);
    List<RecursoAcessibilidadeJpa> findByEventoId(UUID eventoId);
}

@Repository
class RecursoAcessibilidadeRepositorioImpl implements IRecursoAcessibilidadeRepositorio {

    private final RecursoAcessibilidadeJpaRepository jpa;
    private final JpaMapeador mapeador;

    RecursoAcessibilidadeRepositorioImpl(RecursoAcessibilidadeJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(RecursoAcessibilidade recurso) {
        jpa.save(mapeador.map(recurso, RecursoAcessibilidadeJpa.class));
    }

    @Override
    public Optional<RecursoAcessibilidade> obter(UUID id) {
        return jpa.findById(id).map(r -> mapeador.map(r, RecursoAcessibilidade.class));
    }

    @Override
    public List<RecursoAcessibilidade> listarPorApresentacao(UUID apresentacaoId) {
        return jpa.findByApresentacaoId(apresentacaoId).stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public List<RecursoAcessibilidade> listarAtivosPorEvento(UUID eventoId) {
        return jpa.findByEventoIdAndStatus(eventoId, StatusRecurso.CONFIRMADO).stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public List<RecursoAcessibilidade> listarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public List<RecursoAcessibilidade> listarTodos() {
        return jpa.findAll().stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public void atualizar(RecursoAcessibilidade recurso) {
        jpa.save(mapeador.map(recurso, RecursoAcessibilidadeJpa.class));
    }
}
