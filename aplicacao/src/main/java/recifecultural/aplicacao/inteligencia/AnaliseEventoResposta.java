package recifecultural.aplicacao.inteligencia;

public class AnaliseEventoResposta {

    public enum NivelRisco { ALTO, MEDIO, BAIXO }

    private final double receitaProjetada;
    private final double taxaOcupacaoEsperada;
    private final String publicoAlvoMaiorAdesao;
    private final NivelRisco riscoCancelamento;

    public AnaliseEventoResposta(double receitaProjetada,
                                 double taxaOcupacaoEsperada,
                                 String publicoAlvoMaiorAdesao,
                                 NivelRisco riscoCancelamento) {
        this.receitaProjetada = receitaProjetada;
        this.taxaOcupacaoEsperada = taxaOcupacaoEsperada;
        this.publicoAlvoMaiorAdesao = publicoAlvoMaiorAdesao;
        this.riscoCancelamento = riscoCancelamento;
    }

    public double getReceitaProjetada() { return receitaProjetada; }
    public double getTaxaOcupacaoEsperada() { return taxaOcupacaoEsperada; }
    public String getPublicoAlvoMaiorAdesao() { return publicoAlvoMaiorAdesao; }
    public NivelRisco getRiscoCancelamento() { return riscoCancelamento; }
}
