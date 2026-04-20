package recifecultural.dominio.agenda.evento;

import java.math.BigDecimal;

public final class Preco {
    private final BigDecimal inteira;
    private final BigDecimal meia;
    private final String social;

    public Preco(BigDecimal inteira, BigDecimal meia, String social) {
        if (inteira != null && inteira.signum() < 0)
            throw new IllegalArgumentException("Preço negativo.");
        if (meia != null && inteira != null && meia.compareTo(inteira) > 0)
            throw new IllegalArgumentException("Meia entrada não pode ser maior que a inteira.");
        this.inteira = inteira;
        this.meia = meia;
        this.social = social;
    }

    public BigDecimal getInteira() { return inteira; }
    public BigDecimal getMeia()    { return meia; }
    public String getSocial()      { return social; }
}
