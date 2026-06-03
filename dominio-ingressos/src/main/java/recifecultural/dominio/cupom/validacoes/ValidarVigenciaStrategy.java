package recifecultural.dominio.cupom.validacoes;

import recifecultural.dominio.cupom.Cupom;
import org.apache.commons.lang3.Validate;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ValidarVigenciaStrategy implements ValidacaoCupomStrategy {
    @Override
    public void validar(Cupom c, String cpf, BigDecimal valor, String cat, LocalDateTime data) {
        Validate.isTrue(data.isAfter(c.getDataInicio()) && data.isBefore(c.getDataFim()),
                "Cupom expirado ou ainda não iniciado.");
    }
}