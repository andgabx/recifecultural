package recifecultural.dominio.catraca;

import lombok.Value;
import org.apache.commons.lang3.Validate;

@Value
public class IngressoCatracaId {
    String valor;

    public IngressoCatracaId(String valor) {
        Validate.notBlank(valor, "O ID do ingresso não pode ser vazio.");
        this.valor = valor;
    }
}