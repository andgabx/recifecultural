package recifecultural.infraestrutura.persistencia.patrocinio;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.patrocinio.StatusPatrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "patrocinio")
public class PatrocinioJpa {
    @Id
    UUID id;
    UUID eventoId;
    String patrocinadorNome;
    String categoriaPatrocinio;
    String tipo;
    String modalidade;
    BigDecimal valorContribuicao;
    LocalDateTime dataEvento;
    @Enumerated(EnumType.STRING)
    StatusPatrocinio status;
    BigDecimal valorReembolsado;
    BigDecimal multaAplicada;
}

public interface PatrocinioJpaRepository extends JpaRepository<PatrocinioJpa, UUID> {
    Optional<PatrocinioJpa> findByEventoIdAndTipo(UUID eventoId, String tipo);
    Optional<PatrocinioJpa> findByEventoIdAndCategoriaPatrocinio(UUID eventoId, String categoria);
    List<PatrocinioJpa> findByEventoId(UUID eventoId);
}
