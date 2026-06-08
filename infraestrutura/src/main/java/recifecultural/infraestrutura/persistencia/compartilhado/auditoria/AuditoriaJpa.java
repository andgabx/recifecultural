package recifecultural.infraestrutura.persistencia.compartilhado.auditoria;

import jakarta.persistence.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.compartilhado.auditoria.AcaoAuditoria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "auditoria")
public class AuditoriaJpa {
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
