package recifecultural.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import recifecultural.agenda.bloqueioadministrativo.BloqueioAdministrativoServicoAplicacao;
import recifecultural.agenda.notificacao.NotificacaoServicoAplicacao;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.notificacao.INotificacaoRepositorio;
import recifecultural.dominio.agenda.notificacao.NotificacaoServico;

@Configuration
public class ConfiguracaoDominio {

    @Bean
    public BloqueioAdministrativoServico bloqueioAdministrativoServico(
            IBloqueioAdministrativoRepositorio bloqueioRepo,
            IEventoRepositorio eventoRepo) {
        return new BloqueioAdministrativoServico(bloqueioRepo, eventoRepo);
    }

    @Bean
    public BloqueioAdministrativoServicoAplicacao bloqueioAdministrativoServicoAplicacao(
            BloqueioAdministrativoServico bloqueioAdministrativoServico) {
        return new BloqueioAdministrativoServicoAplicacao(bloqueioAdministrativoServico);
    }

    @Bean
    public NotificacaoServico notificacaoServico(INotificacaoRepositorio notificacaoRepo) {
        return new NotificacaoServico(notificacaoRepo);
    }

    @Bean
    public NotificacaoServicoAplicacao notificacaoServicoAplicacao(NotificacaoServico notificacaoServico) {
        return new NotificacaoServicoAplicacao(notificacaoServico);
    }
}