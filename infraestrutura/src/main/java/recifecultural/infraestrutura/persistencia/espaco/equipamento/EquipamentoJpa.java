package recifecultural.infraestrutura.persistencia.espaco.equipamento;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.agenda.equipamento.StatusEquipamento;

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
}

interface EquipamentoJpaRepository extends JpaRepository<EquipamentoJpa, UUID> {
    @Query("SELECT e FROM EquipamentoJpa e WHERE e.espacoId = :espacoId AND e.nome = :nome AND e.status = 'DISPONIVEL'")
    List<EquipamentoJpa> findDisponiveisPorEspacoENome(UUID espacoId, String nome);

    List<EquipamentoJpa> findByEspacoId(UUID espacoId);

    List<EquipamentoJpa> findByEventoAlocadoId(UUID eventoId);
}
