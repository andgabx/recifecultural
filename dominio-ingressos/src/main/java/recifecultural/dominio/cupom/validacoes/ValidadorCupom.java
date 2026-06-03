package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface ValidadorCupom {
    void validar(Cupom cupom, String cpfUsuario, BigDecimal valorPedido, String categoriaEvento, LocalDateTime dataAtual);
}