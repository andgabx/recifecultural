package recifecultural.infraestrutura.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import recifecultural.dominio.agenda.prereserva.PreReservaServico;
import recifecultural.dominio.espaco.suporte.SuporteTecnicoServico;

import java.time.LocalDateTime;

@Component
class AgendaScheduler {

    private final PreReservaServico preReservaServico;
    private final SuporteTecnicoServico suporteTecnicoServico;

    @Value("${recifecultural.scheduler.chamado.sla-horas:24}")
    private long chamadoSlaHoras;

    AgendaScheduler(PreReservaServico preReservaServico, SuporteTecnicoServico suporteTecnicoServico) {
        this.preReservaServico = preReservaServico;
        this.suporteTecnicoServico = suporteTecnicoServico;
    }

    @Scheduled(fixedDelayString = "${recifecultural.scheduler.prereserva.intervalo-ms:60000}")
    void expirarPreReservasVencidas() {
        preReservaServico.expirarVencidas();
    }

    @Scheduled(fixedDelayString = "${recifecultural.scheduler.chamado.intervalo-ms:3600000}")
    void escalarChamadosVencidos() {
        suporteTecnicoServico.escalarVencidos(LocalDateTime.now(), chamadoSlaHoras);
    }
}
