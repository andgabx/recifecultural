package recifecultural.infraestrutura.persistencia.financeiro;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.financeiro.StatusOrcamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orcamento_periodo")
public class OrcamentoPeriodoJpa {
    @Id
    UUID id;
    LocalDateTime periodoInicio;
    LocalDateTime periodoFim;
    BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    StatusOrcamento status;
}

interface OrcamentoPeriodoJpaRepository extends JpaRepository<OrcamentoPeriodoJpa, UUID> {
    @Query("SELECT o FROM OrcamentoPeriodoJpa o WHERE o.periodoInicio <= :fim AND o.periodoFim >= :inicio")
    OrcamentoPeriodoJpa findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
}
