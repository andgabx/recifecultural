package recifecultural.aplicacao.inteligencia;

import org.springframework.stereotype.Service;

import java.util.UUID;

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
     * Simula um modelo para predição de faltas (No-Show) geral de um evento.
     * Futuramente: Integrar com modelo de ML (ex: Tribuo, ONNX Runtime, ou API Python)
     * para avaliar dados históricos do evento, características temporais e clima.
     */
    public PrevisaoNoShowResposta preverNoShow(UUID eventoId) {
        if (eventoId == null) {
            throw new IllegalArgumentException("eventoId é obrigatório.");
        }

        // Criando uma pseudo-aleatoriedade baseada no ID do evento para resultados reproduzíveis
        long seed = Math.abs(eventoId.getMostSignificantBits() ^ eventoId.getLeastSignificantBits());

        // Simulando uma probabilidade de no-show em formato percentual (ex: entre 5% e 35%)
        double probabilidadeNoShow = 5.0 + (seed % 30);
        double probabilidadeFormatada = Math.round(probabilidadeNoShow * 100.0) / 100.0;

        // Mantendo o mock do alerta de alto risco (ex: no-show > 20% aciona o alerta)
        boolean alertaAltoRisco = probabilidadeFormatada > 20.0;

        return new PrevisaoNoShowResposta(
                eventoId,
                probabilidadeFormatada,
                alertaAltoRisco
        );
    }

    /**
     * Simula uma análise estratégica do evento combinando os modelos de receita,
     * ocupação e segmentação de público. Determinístico pelo eventoId para que
     * a mesma análise seja reproduzível durante a fase de simulação.
     * Futuramente: Cruzar histórico de vendas, perfil demográfico de compradores
     * e modelo de churn por categoria.
     */
    public AnaliseEventoResposta analisarEvento(UUID eventoId) {
        if (eventoId == null) {
            throw new IllegalArgumentException("eventoId é obrigatório.");
        }

        long seed = Math.abs(eventoId.getMostSignificantBits() ^ eventoId.getLeastSignificantBits());

        double receitaProjetada = Math.round((25_000.0 + (seed % 75_000)) * 100.0) / 100.0;
        double taxaOcupacaoEsperada = 50.0 + (seed % 50);

        String[] publicos = { "Jovens 18-25", "Adultos 26-35", "Famílias", "Profissionais 30-45", "Estudantes" };
        String publicoAlvo = publicos[(int) (seed % publicos.length)];

        AnaliseEventoResposta.NivelRisco risco;
        if (taxaOcupacaoEsperada >= 80) {
            risco = AnaliseEventoResposta.NivelRisco.BAIXO;
        } else if (taxaOcupacaoEsperada >= 65) {
            risco = AnaliseEventoResposta.NivelRisco.MEDIO;
        } else {
            risco = AnaliseEventoResposta.NivelRisco.ALTO;
        }

        return new AnaliseEventoResposta(receitaProjetada, taxaOcupacaoEsperada, publicoAlvo, risco);
    }
}