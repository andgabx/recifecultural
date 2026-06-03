package recifecultural.infraestrutura.persistencia.financeiro;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.financeiro.CategoriaDespesa;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "despesa")
public class DespesaJpa {
    @Id
    UUID id;
    UUID orcamentoId;
    String descricao;
    BigDecimal valor;
    @Enumerated(EnumType.STRING)
    CategoriaDespesa categoria;
    LocalDateTime dataRegistro;
}

public interface DespesaJpaRepository extends JpaRepository<DespesaJpa, UUID> {
    List<DespesaJpa> findByOrcamentoId(UUID orcamentoId);

    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM DespesaJpa d WHERE d.orcamentoId = :orcamentoId")
    BigDecimal somarPorOrcamento(UUID orcamentoId);
}
