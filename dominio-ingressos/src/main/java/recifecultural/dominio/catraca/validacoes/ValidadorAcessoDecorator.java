package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import java.time.LocalDateTime;

public abstract class ValidadorAcessoDecorator implements ValidadorAcesso {
    protected final ValidadorAcesso validadorInterno;

    public ValidadorAcessoDecorator(ValidadorAcesso validadorInterno) {
        this.validadorInterno = validadorInterno;
    }

    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
        if (validadorInterno != null) {
            validadorInterno.validar(ingresso, horaAcesso, portaoFisico);
        }
    }
}