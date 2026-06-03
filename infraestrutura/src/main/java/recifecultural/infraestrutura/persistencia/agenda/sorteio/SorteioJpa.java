package recifecultural.infraestrutura.persistencia.agenda.sorteio;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.agenda.sorteio.StatusInscricao;
import recifecultural.dominio.agenda.sorteio.StatusSorteio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sorteio")
public class SorteioJpa {
    @Id
    UUID id;
    UUID apresentacaoId;
    UUID eventoId;
    int vagas;
    LocalDateTime prazoInscricao;
    LocalDateTime dataApresentacao;
    @Enumerated(EnumType.STRING)
    StatusSorteio status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sorteio_inscricao", joinColumns = @JoinColumn(name = "sorteio_id"))
    List<InscricaoJpa> inscricoes = new ArrayList<>();
}

@Embeddable
public class InscricaoJpa {
    UUID espectadorId;
    LocalDateTime momentoInscricao;
    @Enumerated(EnumType.STRING)
    StatusInscricao status;
}

interface SorteioJpaRepository extends JpaRepository<SorteioJpa, UUID> {
    List<SorteioJpa> findByEventoId(UUID eventoId);
    List<SorteioJpa> findByStatus(StatusSorteio status);

    @Query("SELECT DISTINCT s FROM SorteioJpa s JOIN s.inscricoes i WHERE i.espectadorId = :espectadorId")
    List<SorteioJpa> findByEspectadorId(UUID espectadorId);
}
