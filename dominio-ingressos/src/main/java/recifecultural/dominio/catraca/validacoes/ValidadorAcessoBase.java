package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import java.time.LocalDateTime;

public class ValidadorAcessoBase implements ValidadorAcesso {
    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
    }
}