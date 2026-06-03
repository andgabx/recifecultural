package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.TipoIngresso;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ValidarToleranciaAtrasoDecorator extends ValidadorAcessoDecorator {

    public ValidarToleranciaAtrasoDecorator(ValidadorAcesso validadorInterno) {
        super(validadorInterno);
    }

    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
        super.validar(ingresso, horaAcesso, portaoFisico);

        if (ingresso.getTipoIngresso() == TipoIngresso.COMUM) {
            long minutosAtraso = ChronoUnit.MINUTES.between(ingresso.getHorarioInicioEvento(), horaAcesso);

            Validate.isTrue(minutosAtraso <= 15,
                    "Entrada Negada: O limite de 15 minutos de atraso foi excedido. As portas do teatro estão fechadas.");
        }
    }
}