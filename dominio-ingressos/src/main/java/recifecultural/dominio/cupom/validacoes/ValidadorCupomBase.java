package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidadorCupomBase implements ValidadorCupom {
    @Override
    public void validar(Cupom cupom, String cpfUsuario, BigDecimal valorPedido, String categoriaEvento, LocalDateTime dataAtual) {
    }
}