package recifecultural.dominio.catraca;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;

@Getter
public class IngressoCatraca {
    private final IngressoCatracaId id;
    private final String idEvento;
    private StatusIngressoCatraca status;
    private final LocalDateTime horarioInicioEvento;


    private final TipoIngresso tipoIngresso;
    private final String portaoAcesso;

    public IngressoCatraca(IngressoCatracaId id, String idEvento, StatusIngressoCatraca status,
                           LocalDateTime horarioInicioEvento, TipoIngresso tipoIngresso, String portaoAcesso) {
        Validate.notNull(id, "ID do ingresso é obrigatório.");
        Validate.notBlank(idEvento, "ID do evento é obrigatório.");
        Validate.notNull(status, "Status inicial é obrigatório.");
        Validate.notNull(horarioInicioEvento, "O horário do evento é obrigatório.");
        Validate.notNull(tipoIngresso, "O tipo de ingresso é obrigatório.");
        Validate.notBlank(portaoAcesso, "O portão de acesso é obrigatório.");

        this.id = id;
        this.idEvento = idEvento;
        this.status = status;
        this.horarioInicioEvento = horarioInicioEvento;
        this.tipoIngresso = tipoIngresso;
        this.portaoAcesso = portaoAcesso;
    }
    public void registrarEntrada(LocalDateTime horarioLeituraCatraca, String portaoDaCatracaFisica) {


        Validate.isTrue(this.status != StatusIngressoCatraca.CANCELADO_OU_REEMBOLSADO,
                "Entrada Negada: Este ingresso consta como cancelado ou reembolsado.");
        Validate.isTrue(this.status != StatusIngressoCatraca.UTILIZADO,
                "ALERTA FRAUDE: Este ingresso já foi utilizado.");


        Validate.isTrue(this.portaoAcesso.equalsIgnoreCase(portaoDaCatracaFisica),
                "Acesso Negado: Este ingresso pertence ao " + this.portaoAcesso + ". Dirija-se ao local correto.");

        LocalDateTime limiteAbertura = horarioInicioEvento.minusHours(1);
        Validate.isTrue(horarioLeituraCatraca.isAfter(limiteAbertura) || horarioLeituraCatraca.isEqual(limiteAbertura),
                "Os portões ainda não estão abertos para este evento. Abertura 1 hora antes do início.");


        if (this.tipoIngresso != TipoIngresso.VIP) {
            LocalDateTime limiteFechamento = horarioInicioEvento.plusMinutes(15);
            Validate.isTrue(horarioLeituraCatraca.isBefore(limiteFechamento) || horarioLeituraCatraca.isEqual(limiteFechamento),
                    "Entrada Negada: O limite de 15 minutos de atraso foi excedido. As portas do teatro estão fechadas.");
        }


        this.status = StatusIngressoCatraca.UTILIZADO;
    }
}