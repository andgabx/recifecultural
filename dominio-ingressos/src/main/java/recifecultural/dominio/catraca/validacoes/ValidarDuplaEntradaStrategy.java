package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.StatusIngressoCatraca;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;

public class ValidarDuplaEntradaStrategy implements ValidacaoAcessoStrategy {
    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
        Validate.isTrue(ingresso.getStatus() != StatusIngressoCatraca.UTILIZADO,
                "ALERTA FRAUDE: Este ingresso já foi utilizado.");
    }
}