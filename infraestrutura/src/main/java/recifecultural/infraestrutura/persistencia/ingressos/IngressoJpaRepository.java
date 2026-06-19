package recifecultural.infraestrutura.persistencia.ingressos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IngressoJpaRepository extends JpaRepository<IngressoJpa, UUID> {
    IngressoJpa findByCodigoQr(String codigoQr);

    @Query("SELECT COUNT(i) FROM IngressoJpa i WHERE i.eventoId = :eventoId AND i.dataHoraApresentacao = :dataHora AND i.status = 'ATIVO'")
    int countAtivosPorApresentacao(UUID eventoId, LocalDateTime dataHora);

    @Query("SELECT i FROM IngressoJpa i WHERE i.dataHoraApresentacao >= :inicio AND i.dataHoraApresentacao <= :fim")
    List<IngressoJpa> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<IngressoJpa> findByEventoId(UUID eventoId);

    @Query("SELECT i.assentoId FROM IngressoJpa i WHERE i.eventoId = :eventoId AND i.assentoId IS NOT NULL AND i.status IN (recifecultural.dominio.ingressos.StatusIngresso.ATIVO, recifecultural.dominio.ingressos.StatusIngresso.UTILIZADO)")
    List<UUID> findAssentosOcupadosByEventoId(@Param("eventoId") UUID eventoId);
}
