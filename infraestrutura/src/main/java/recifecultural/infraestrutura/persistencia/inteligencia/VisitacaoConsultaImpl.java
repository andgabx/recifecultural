package recifecultural.infraestrutura.persistencia.inteligencia;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import recifecultural.aplicacao.inteligencia.VisitacaoConsulta;
import recifecultural.aplicacao.inteligencia.VisitacaoMensal;

import java.util.List;

/**
 * Implementação JPA da consulta de visitação agregada.
 *
 * Usa native query porque EXTRACT(MONTH FROM ...) é mais limpo que via JPQL,
 * que exigiria FUNCTION('EXTRACT', ...) com tipos opacos.
 */
@Repository
public class VisitacaoConsultaImpl implements VisitacaoConsulta {

    @PersistenceContext
    private EntityManager em;

    private static final String SQL = """
        SELECT e.nome AS teatro,
               CAST(EXTRACT(MONTH FROM i.data_hora_apresentacao) AS INTEGER) AS mes,
               COUNT(*) AS visitantes
        FROM ingresso i
        JOIN evento ev ON i.evento_id = ev.id
        JOIN espaco e ON ev.local_id = e.id
        WHERE i.status IN ('ATIVO', 'UTILIZADO')
          AND i.data_hora_apresentacao IS NOT NULL
        GROUP BY e.nome, EXTRACT(MONTH FROM i.data_hora_apresentacao)
        ORDER BY e.nome, mes
        """;

    @Override
    @SuppressWarnings("unchecked")
    public List<VisitacaoMensal> agregarPorEspacoEMes() {
        List<Object[]> linhas = em.createNativeQuery(SQL).getResultList();
        return linhas.stream()
                .map(r -> new VisitacaoMensal(
                        (String) r[0],
                        ((Number) r[1]).intValue(),
                        ((Number) r[2]).longValue()))
                .toList();
    }
}
