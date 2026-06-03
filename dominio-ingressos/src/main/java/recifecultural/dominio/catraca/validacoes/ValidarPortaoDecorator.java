package recifecultural.dominio.catraca.validacoes;

import recifecultural.dominio.catraca.IngressoCatraca;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;

public class ValidarPortaoDecorator extends ValidadorAcessoDecorator {

    public ValidarPortaoDecorator(ValidadorAcesso validadorInterno) {
        super(validadorInterno);
    }

    @Override
    public void validar(IngressoCatraca ingresso, LocalDateTime horaAcesso, String portaoFisico) {
        super.validar(ingresso, horaAcesso, portaoFisico);

        Validate.isTrue(ingresso.getPortaoAcesso().equals(portaoFisico),
                "Acesso Negado: Este ingresso pertence ao " + ingresso.getPortaoAcesso() + ". Dirija-se ao local correto.");
    }
}