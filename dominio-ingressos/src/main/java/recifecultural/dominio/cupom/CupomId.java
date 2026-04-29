package recifecultural.dominio.cupom;

import org.apache.commons.lang3.Validate;

public class CupomId {
    private final String valor;

    public CupomId(String valor) {
        Validate.notBlank(valor, "O identificador do cupom não pode ser vazio.");
        this.valor = valor;
    }

    public String getValor() { return valor; }
}