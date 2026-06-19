package recifecultural.aplicacao.inteligencia;

import java.util.List;

public interface VisitacaoConsulta {
    /**
     * Retorna visitação agregada por espaço (teatro) e mês (1..12),
     * somando ingressos ATIVO ou UTILIZADO de todos os anos disponíveis.
     */
    List<VisitacaoMensal> agregarPorEspacoEMes();
}
