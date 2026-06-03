package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidarCategoriaDecorator extends ValidadorCupomDecorator {
    public ValidarCategoriaDecorator(ValidadorCupom validadorInterno) { super(validadorInterno); }

    @Override
    public void validar(Cupom c, String cpf, BigDecimal valor, String cat, LocalDateTime data) {
        super.validar(c, cpf, valor, cat, data);
        if (c.getCategoriaPermitida() != null && !c.getCategoriaPermitida().isBlank()) {
            Validate.isTrue(c.getCategoriaPermitida().equalsIgnoreCase(cat),
                    "Cupom inválido para a categoria " + cat);
        }
    }
}