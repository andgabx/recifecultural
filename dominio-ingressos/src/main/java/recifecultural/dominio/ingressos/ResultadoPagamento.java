package recifecultural.dominio.ingressos;

import java.math.BigDecimal;

public final class ResultadoPagamento {

    private final String codigoTransacao;
    private final boolean aprovado;

    public ResultadoPagamento(String codigoTransacao, boolean aprovado) {
        this.codigoTransacao = codigoTransacao;
        this.aprovado = aprovado;
    }

    public String getCodigoTransacao() {
        return codigoTransacao;
    }

    public boolean isAprovado() {
        return aprovado;
    }
}
