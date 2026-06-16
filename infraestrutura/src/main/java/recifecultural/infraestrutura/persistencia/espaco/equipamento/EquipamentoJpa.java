package recifecultural.infraestrutura.persistencia.espaco.equipamento;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import recifecultural.dominio.agenda.equipamento.StatusEquipamento;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "equipamento")
public class EquipamentoJpa {
    @Id
    UUID id;
    UUID espacoId;
    String nome;
    @Enumerated(EnumType.STRING)
    StatusEquipamento status;
    UUID eventoAlocadoId;
    LocalDate alocacaoInicio;
    LocalDate alocacaoFim;

    public LocalDate getAlocacaoInicio() { return alocacaoInicio; }
    public void setAlocacaoInicio(LocalDate alocacaoInicio) { this.alocacaoInicio = alocacaoInicio; }
    public LocalDate getAlocacaoFim() { return alocacaoFim; }
    public void setAlocacaoFim(LocalDate alocacaoFim) { this.alocacaoFim = alocacaoFim; }
}

interface EquipamentoJpaRepository extends JpaRepository<EquipamentoJpa, UUID> {
    @Query("SELECT e FROM EquipamentoJpa e WHERE e.espacoId = :espacoId AND e.nome = :nome AND e.status = 'DISPONIVEL'")
    List<EquipamentoJpa> findDisponiveisPorEspacoENome(UUID espacoId, String nome);

    @Query("SELECT e FROM EquipamentoJpa e WHERE e.espacoId = :espacoId AND e.nome = :nome AND (e.status = 'DISPONIVEL' OR (e.status = 'ALOCADO' AND (e.alocacaoFim < :inicio OR e.alocacaoInicio > :fim)))")
    List<EquipamentoJpa> findDisponiveisPorEspacoENomeEData(@Param("espacoId") UUID espacoId, @Param("nome") String nome, @Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    List<EquipamentoJpa> findByEspacoId(UUID espacoId);

    List<EquipamentoJpa> findByEventoAlocadoId(UUID eventoId);
}
