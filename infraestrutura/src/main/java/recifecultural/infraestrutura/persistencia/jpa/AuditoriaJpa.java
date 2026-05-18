package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.compartilhado.auditoria.AcaoAuditoria;
import recifecultural.dominio.compartilhado.auditoria.IAuditoriaRepositorio;
import recifecultural.dominio.compartilhado.auditoria.RegistroAuditoria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auditoria")
class AuditoriaJpa {
    @Id
    UUID id;
    String entidade;
    UUID entidadeId;
    @Enumerated(EnumType.STRING)
    AcaoAuditoria acao;
    String statusAnterior;
    String statusNovo;
    @Column(length = 2000)
    String descricao;
    LocalDateTime momento;
}

interface AuditoriaJpaRepository extends JpaRepository<AuditoriaJpa, UUID> {
    List<AuditoriaJpa> findAllByOrderByMomentoDesc(PageRequest page);
    List<AuditoriaJpa> findByEntidadeOrderByMomentoDesc(String entidade, PageRequest page);
}

@Repository
class AuditoriaRepositorioImpl implements IAuditoriaRepositorio {

    private final AuditoriaJpaRepository jpa;
    private final JpaMapeador mapeador;

    AuditoriaRepositorioImpl(AuditoriaJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void registrar(RegistroAuditoria registro) {
        jpa.save(mapeador.map(registro, AuditoriaJpa.class));
    }

    @Override
    public List<RegistroAuditoria> listarRecentes(int limite) {
        return jpa.findAllByOrderByMomentoDesc(PageRequest.of(0, Math.max(1, limite))).stream()
                .map(j -> mapeador.map(j, RegistroAuditoria.class))
                .toList();
    }

    @Override
    public List<RegistroAuditoria> listarPorEntidade(String entidade, int limite) {
        return jpa.findByEntidadeOrderByMomentoDesc(entidade, PageRequest.of(0, Math.max(1, limite))).stream()
                .map(j -> mapeador.map(j, RegistroAuditoria.class))
                .toList();
    }
}
