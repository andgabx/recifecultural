package recifecultural.dominio.catraca;

import org.apache.commons.lang3.Validate;

public class IngressoCatracaId {
    private final String valor;

    public IngressoCatracaId(String valor) {
        Validate.notBlank(valor, "O ID do ingresso não pode ser vazio.");
        this.valor = valor;
    }

    public String getValor() { return valor; }
}