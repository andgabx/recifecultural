package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;

public class ResultadoSubsidio {

    private final BigDecimal novoPrecoSocial;
    private final boolean pisoAplicado;

    public ResultadoSubsidio(BigDecimal novoPrecoSocial, boolean pisoAplicado) {
        this.novoPrecoSocial = novoPrecoSocial;
        this.pisoAplicado = pisoAplicado;
    }

    public BigDecimal getNovoPrecoSocial() { return novoPrecoSocial; }
    public boolean isPisoAplicado() { return pisoAplicado; }
}
