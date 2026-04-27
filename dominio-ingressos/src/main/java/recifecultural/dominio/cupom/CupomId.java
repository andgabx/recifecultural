package recifecultural.dominio.cupom;

import lombok.Value;
import org.apache.commons.lang3.Validate;

@Value
public class CupomId {
    String valor;

    public CupomId(String valor) {
        Validate.notBlank(valor, "O identificador do cupom não pode ser vazio.");
        this.valor = valor;
    }
}