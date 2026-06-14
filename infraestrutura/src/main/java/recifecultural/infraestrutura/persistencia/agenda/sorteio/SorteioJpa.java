package recifecultural.infraestrutura.persistencia.agenda.sorteio;

import jakarta.persistence.*;
import org.springframework.data.domain.Pageable;
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
class InscricaoJpa {
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

    @Query("""
            SELECT i
            FROM SorteioJpa s JOIN s.inscricoes i
            WHERE s.id = :sorteioId
            ORDER BY
                CASE
                    WHEN i.status = recifecultural.dominio.agenda.sorteio.StatusInscricao.GANHADOR THEN 0
                    WHEN i.status = recifecultural.dominio.agenda.sorteio.StatusInscricao.SUPLENTE THEN 1
                    WHEN i.status = recifecultural.dominio.agenda.sorteio.StatusInscricao.INSCRITO THEN 2
                    WHEN i.status = recifecultural.dominio.agenda.sorteio.StatusInscricao.DESISTENTE THEN 3
                    WHEN i.status = recifecultural.dominio.agenda.sorteio.StatusInscricao.CANCELADA THEN 4
                    ELSE 5
                END,
                i.momentoInscricao
            """)
    List<InscricaoJpa> findInscricoesOrdenadas(UUID sorteioId, Pageable pageable);
}
