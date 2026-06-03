package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import java.time.LocalDateTime;

public interface ValidadorAcesso {
    void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico);
}