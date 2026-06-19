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

    /**
     * Para cada apresentação futura de qualquer evento no espaço indicado, conta os
     * ingressos ATIVO e retorna a maior dessas contagens (ou 0 quando não há nenhuma).
     * Usado para validar redução de capacidade sem confiar no valor enviado pelo cliente.
     */
    @Query("""
            SELECT COALESCE(MAX(cnt), 0) FROM (
                SELECT COUNT(i) AS cnt
                FROM IngressoJpa i
                JOIN recifecultural.infraestrutura.persistencia.agenda.evento.EventoJpa e ON e.id = i.eventoId
                WHERE e.localId = :espacoId
                  AND i.dataHoraApresentacao > :agora
                  AND i.status = recifecultural.dominio.ingressos.StatusIngresso.ATIVO
                GROUP BY i.eventoId, i.dataHoraApresentacao
            ) sub
            """)
    int findMaiorCargaAtivosPorEspaco(@Param("espacoId") UUID espacoId, @Param("agora") LocalDateTime agora);
}
