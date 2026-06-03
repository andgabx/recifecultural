package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidarCategoriaStrategy implements ValidacaoCupomStrategy {
    @Override
    public void validar(Cupom c, String cpf, BigDecimal valor, String cat, LocalDateTime data) {
        if (c.getCategoriaPermitida() != null && !c.getCategoriaPermitida().isBlank()) {
            Validate.isTrue(c.getCategoriaPermitida().equalsIgnoreCase(cat),
                    "Cupom inválido para a categoria " + cat);
        }
    }
}