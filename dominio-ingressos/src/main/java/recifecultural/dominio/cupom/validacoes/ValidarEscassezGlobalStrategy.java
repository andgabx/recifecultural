package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidarEscassezGlobalStrategy implements ValidacaoCupomStrategy {
    @Override
    public void validar(Cupom c, String cpf, BigDecimal valor, String cat, LocalDateTime data) {
        Validate.isTrue(c.getUsosGlobais() < c.getLimiteGlobal(),
                "Limite global atingido.");
    }
}