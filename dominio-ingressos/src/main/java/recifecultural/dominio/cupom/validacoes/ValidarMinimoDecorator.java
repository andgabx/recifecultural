package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidarMinimoDecorator extends ValidadorCupomDecorator {
    public ValidarMinimoDecorator(ValidadorCupom validadorInterno) { super(validadorInterno); }

    @Override
    public void validar(Cupom c, String cpf, BigDecimal valor, String cat, LocalDateTime data) {
        super.validar(c, cpf, valor, cat, data);
        Validate.isTrue(valor.compareTo(c.getValorMinimoPedido()) >= 0,
                "Pedido abaixo do valor mínimo de R$ " + c.getValorMinimoPedido());
    }
}