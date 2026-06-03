package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.StatusIngressoCatraca;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;

public class ValidarDuplaEntradaDecorator extends ValidadorAcessoDecorator {

    public ValidarDuplaEntradaDecorator(ValidadorAcesso validadorInterno) {
        super(validadorInterno);
    }

    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
        super.validar(ingresso, horaAcesso, portaoFisico);

        Validate.isTrue(ingresso.getStatus() != StatusIngressoCatraca.UTILIZADO,
                "ALERTA FRAUDE: Este ingresso já foi utilizado.");
    }
}