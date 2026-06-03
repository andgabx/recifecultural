package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidarLimiteCpfDecorator extends ValidadorCupomDecorator {
    public ValidarLimiteCpfDecorator(ValidadorCupom validadorInterno) { super(validadorInterno); }

    @Override
    public void validar(Cupom c, String cpfUsuario, BigDecimal valor, String cat, LocalDateTime data) {
        super.validar(c, cpfUsuario, valor, cat, data);
        long usosAtuaisCpf = c.getCpfsQueJaUsaram().stream().filter(cpf -> cpf.equals(cpfUsuario)).count();
        Validate.isTrue(usosAtuaisCpf < c.getLimitePorCpf(),
                "Limite por CPF atingido.");
    }
}