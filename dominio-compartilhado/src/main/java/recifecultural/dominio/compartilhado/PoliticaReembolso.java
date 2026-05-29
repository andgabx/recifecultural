package recifecultural.dominio.compartilhado;

public class PoliticaReembolso {

    private final int diasParaReembolsoTotal;
    private final int diasMinimoParaReembolsoParcial;
    private final int percentualReembolsoParcial;

    public PoliticaReembolso(int diasParaReembolsoTotal, int diasMinimoParaReembolsoParcial, int percentualReembolsoParcial) {
        if (diasParaReembolsoTotal <= 0)
            throw new IllegalArgumentException("Dias para reembolso total deve ser maior que zero.");
        if (diasMinimoParaReembolsoParcial <= 0)
            throw new IllegalArgumentException("Dias mínimo para reembolso parcial deve ser maior que zero.");
        if (diasParaReembolsoTotal <= diasMinimoParaReembolsoParcial)
            throw new IllegalArgumentException("Dias para reembolso total deve ser maior que o mínimo para reembolso parcial.");
        if (percentualReembolsoParcial <= 0 || percentualReembolsoParcial >= 100)
            throw new IllegalArgumentException("Percentual de reembolso parcial deve ser entre 1 e 99.");

        this.diasParaReembolsoTotal = diasParaReembolsoTotal;
        this.diasMinimoParaReembolsoParcial = diasMinimoParaReembolsoParcial;
        this.percentualReembolsoParcial = percentualReembolsoParcial;
    }

    public int getDiasParaReembolsoTotal() { return diasParaReembolsoTotal; }
    public int getDiasMinimoParaReembolsoParcial() { return diasMinimoParaReembolsoParcial; }
    public int getPercentualReembolsoParcial() { return percentualReembolsoParcial; }
}
