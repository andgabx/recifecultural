package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public abstract class ValidadorCupomDecorator implements ValidadorCupom {
    protected final ValidadorCupom validadorInterno;

    public ValidadorCupomDecorator(ValidadorCupom validadorInterno) {
        this.validadorInterno = validadorInterno;
    }

    @Override
    public void validar(Cupom cupom, String cpfUsuario, BigDecimal valorPedido, String categoriaEvento, LocalDateTime dataAtual) {
        if (validadorInterno != null) {
            validadorInterno.validar(cupom, cpfUsuario, valorPedido, categoriaEvento, dataAtual);
        }
    }
}