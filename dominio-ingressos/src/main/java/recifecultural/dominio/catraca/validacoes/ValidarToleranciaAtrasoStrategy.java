package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.TipoIngresso;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ValidarToleranciaAtrasoStrategy implements ValidacaoAcessoStrategy {
    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
        if (ingresso.getTipoIngresso() == TipoIngresso.COMUM) {
            long minutosAtraso = ChronoUnit.MINUTES.between(ingresso.getHorarioInicioEvento(), horaAcesso);

            Validate.isTrue(minutosAtraso <= 15,
                    "Entrada Negada: O limite de 15 minutos de atraso foi excedido. As portas do teatro estão fechadas.");
        }
    }
}