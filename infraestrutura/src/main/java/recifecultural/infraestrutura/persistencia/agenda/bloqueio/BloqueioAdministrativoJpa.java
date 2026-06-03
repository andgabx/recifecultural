package recifecultural.infraestrutura.persistencia.agenda.bloqueio;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bloqueio_administrativo")
public class BloqueioAdministrativoJpa {
    @Id
    UUID id;
    UUID espacoId;
    LocalDate dataInicio;
    LocalDate dataFim;
    String justificativa;
    boolean ativo;

    @Column(name = "eventos_cancelados", columnDefinition = "text")
    String eventosCancelados; // comma-separated UUIDs, nullable

    public static List<UUID> parseEventosCancelados(String raw) {
        if (raw == null || raw.isBlank()) return List.of();
        return Arrays.stream(raw.split(","))
                .filter(s -> !s.isBlank())
                .map(UUID::fromString)
                .toList();
    }
}

interface BloqueioAdministrativoJpaRepository extends JpaRepository<BloqueioAdministrativoJpa, UUID> {
    List<BloqueioAdministrativoJpa> findByEspacoId(UUID espacoId);

    @Query("SELECT b FROM BloqueioAdministrativoJpa b WHERE b.ativo = true")
    List<BloqueioAdministrativoJpa> findAtivos();
}
