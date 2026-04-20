package recifecultural.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import recifecultural.dominio.agenda.IEventoRepositorio;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;

@Configuration
public class ConfiguracaoDominio {

    @Bean
    public BloqueioAdministrativoServico bloqueioAdministrativoServico(
            IBloqueioAdministrativoRepositorio bloqueioRepo,
            IEventoRepositorio eventoRepo) {
        return new BloqueioAdministrativoServico(bloqueioRepo, eventoRepo);
    }
}