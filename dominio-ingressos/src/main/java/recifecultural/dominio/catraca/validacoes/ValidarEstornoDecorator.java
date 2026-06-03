package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.StatusIngressoCatraca;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;

public class ValidarEstornoDecorator extends ValidadorAcessoDecorator {

    public ValidarEstornoDecorator(ValidadorAcesso validadorInterno) {
        super(validadorInterno);
    }

    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
        super.validar(ingresso, horaAcesso, portaoFisico);

        Validate.isTrue(ingresso.getStatus() != StatusIngressoCatraca.CANCELADO_OU_REEMBOLSADO,
                "Entrada Negada: Este ingresso consta como cancelado ou reembolsado.");
    }
}