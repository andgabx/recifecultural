package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidarEscassezGlobalDecorator extends ValidadorCupomDecorator {
    public ValidarEscassezGlobalDecorator(ValidadorCupom validadorInterno) { super(validadorInterno); }

    @Override
    public void validar(Cupom c, String cpf, BigDecimal valor, String cat, LocalDateTime data) {
        super.validar(c, cpf, valor, cat, data);
        Validate.isTrue(c.getUsosGlobais() < c.getLimiteGlobal(),
                "Limite global atingido.");
    }
}