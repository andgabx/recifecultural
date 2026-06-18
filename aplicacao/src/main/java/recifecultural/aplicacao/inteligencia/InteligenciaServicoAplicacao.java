package recifecultural.aplicacao.inteligencia;

import org.springframework.stereotype.Service;

@Service
public class InteligenciaServicoAplicacao {

    /**
     * Simula um modelo de regressão linear para previsão de receita.
     * Futuramente: Carregar modelo PMML/ONNX treinado.
     */
    public PrevisaoReceitaResposta preverReceita(double orcamentoMarketing, double patrocinio) {
        double multiplicadorBase = 2.5;
        double receitaEstimada = (orcamentoMarketing + patrocinio) * multiplicadorBase;

        // Arredondando para 2 casas decimais
        double receitaFormatada = Math.round(receitaEstimada * 100.0) / 100.0;
        double investimentoTotal = orcamentoMarketing + patrocinio;

        return new PrevisaoReceitaResposta(investimentoTotal, receitaFormatada);
    }

    /**
     * Simula um modelo Random Forest para predição de faltas.
     * Futuramente: Chamar lib de ML como Tribuo ou ONNX Runtime.
     */
    public PrevisaoNoShowResposta preverNoShow(String ingressoId, int antecedenciaDias, String clima) {
        double probabilidade = 0.15; // Probabilidade base

        if (clima != null && clima.equalsIgnoreCase("chuvoso")) {
            probabilidade += 0.40;
        }
        if (antecedenciaDias > 30) {
            probabilidade += 0.10;
        }

        // Limitador máximo
        probabilidade = Math.min(probabilidade, 0.99);
        double probabilidadeFormatada = Math.round(probabilidade * 100.0) / 100.0;

        return new PrevisaoNoShowResposta(
                ingressoId,
                probabilidadeFormatada,
                probabilidadeFormatada > 0.50
        );
    }
}


