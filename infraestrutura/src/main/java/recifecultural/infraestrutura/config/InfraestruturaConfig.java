package recifecultural.infraestrutura.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.EnableScheduling;

import recifecultural.aplicacao.agenda.acessibilidade.RecursoAcessibilidadeServicoAplicacao;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioAdministrativoRepositorioAplicacao;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioAdministrativoServicoAplicacao;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.BloqueioNotificacaoObservador;
import recifecultural.aplicacao.agenda.bloqueioadministrativo.IngressoNotificacaoObservador;
import recifecultural.aplicacao.agenda.comentario.ComentarioServicoAplicacao;
import recifecultural.aplicacao.agenda.equipamento.EquipamentoServicoAplicacao;
import recifecultural.aplicacao.auditoria.AuditoriaServicoAplicacao;
import recifecultural.aplicacao.agenda.sorteio.SorteioRepositorioAplicacao;
import recifecultural.aplicacao.agenda.sorteio.SorteioServicoAplicacao;
import recifecultural.aplicacao.agenda.evento.EventoRepositorioAplicacao;
import recifecultural.aplicacao.agenda.evento.EventoServicoAplicacao;
import recifecultural.aplicacao.artista.artista.ArtistaRepositorioAplicacao;
import recifecultural.aplicacao.artista.artista.ArtistaServicoAplicacao;
import recifecultural.aplicacao.artista.produtor.ProdutorRepositorioAplicacao;
import recifecultural.aplicacao.artista.produtor.ProdutorServicoAplicacao;
import recifecultural.aplicacao.catraca.CatracaServicoAplicacao;
import recifecultural.aplicacao.financeiro.FinanceiroRepositorioAplicacao;
import recifecultural.aplicacao.financeiro.FinanceiroServicoAplicacao;
import recifecultural.aplicacao.ingressos.CupomServicoAplicacao;
import recifecultural.aplicacao.ingressos.IngressoRepositorioAplicacao;
import recifecultural.aplicacao.ingressos.IngressoServicoAplicacao;
import recifecultural.aplicacao.patrocinio.PatrocinioRepositorioAplicacao;
import recifecultural.aplicacao.patrocinio.PatrocinioServicoAplicacao;
import recifecultural.dominio.agenda.acessibilidade.IRecursoAcessibilidadeRepositorio;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidadeServico;
import recifecultural.dominio.agenda.equipamento.EquipamentoServico;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.agenda.prereserva.IPreReservaRepositorio;
import recifecultural.dominio.agenda.prereserva.PreReservaServico;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;
import recifecultural.dominio.agenda.comentario.ComentarioService;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.sorteio.ISorteioRepositorio;
import recifecultural.dominio.agenda.sorteio.SorteioServico;
import recifecultural.dominio.artista.artista.ArtistaServico;
import recifecultural.dominio.artista.artista.IArtistaRepositorio;
import recifecultural.dominio.artista.produtor.IProdutorRepositorio;
import recifecultural.dominio.artista.produtor.ProdutorServico;
import recifecultural.dominio.catraca.CatracaServico;
import recifecultural.dominio.catraca.ICatracaRepositorio;
import recifecultural.dominio.compartilhado.auditoria.IAuditoriaRepositorio;
import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.IUsuarioContextoServico;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoServico;
import recifecultural.dominio.cupom.AplicarCupomServico;
import recifecultural.dominio.cupom.ICupomRepositorio;
import recifecultural.dominio.espaco.espaco.EspacoServico;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.setor.GestaoAmbienteInternoServico;
import recifecultural.dominio.espaco.setor.ISetorRepositorio;
import recifecultural.dominio.espaco.suporte.IChamadoSuporteRepositorio;
import recifecultural.dominio.espaco.suporte.SuporteTecnicoServico;
import recifecultural.dominio.financeiro.DesempenhoTeatroServico;
import recifecultural.dominio.financeiro.IDespesaRepositorio;
import recifecultural.dominio.financeiro.IOrcamentoRepositorio;
import recifecultural.dominio.financeiro.OrcamentoServico;
import recifecultural.dominio.ingressos.IGatewayPagamento;
import recifecultural.dominio.ingressos.IIngressoRepositorio;
import recifecultural.dominio.ingressos.IngressoServico;
import recifecultural.dominio.patrocinio.IPatrocinioRepositorio;
import recifecultural.dominio.patrocinio.PatrocinioServico;
import recifecultural.infraestrutura.gateway.GatewayPagamentoSimulado;
import recifecultural.infraestrutura.padroes.ComentarioRepositorioComModeracao;
import recifecultural.infraestrutura.padroes.EquipamentoRepositorioProxyCache;
import recifecultural.infraestrutura.padroes.EspacoRepositorioProxyCache;
import recifecultural.infraestrutura.padroes.EventoRepositorioComAuditoria;
import recifecultural.infraestrutura.padroes.GatewayPagamentoComLog;
import recifecultural.infraestrutura.padroes.PatrocinioRepositorioProxyCache;

@Configuration
@EnableScheduling
public class InfraestruturaConfig {

    // ── Stubs para dependências compartilhadas ─────────────────────────────

    @Bean
    NotificacaoServico notificacaoServico(INotificacaoRepositorio notificacaoRepositorio,
                                          IUsuarioContextoServico usuarioContextoServico) {
        return new NotificacaoServico(notificacaoRepositorio, usuarioContextoServico);
    }

    // ── Domain Services ────────────────────────────────────────────────────

    @Bean
    EventoServico eventoServico(IEventoRepositorio eventoRepositorio,
                                 IAuditoriaRepositorio auditoriaRepositorio) {
        // Decorator (Par 4): EventoRepositorioComAuditoria registra trilha de aprovação/reprovação
        return new EventoServico(new EventoRepositorioComAuditoria(eventoRepositorio, auditoriaRepositorio));
    }

    @Bean
    ComentarioService comentarioService(ComentarioRepositorio comentarioRepositorio) {
        // Decorator (Par 4): ComentarioRepositorioComModeracao filtra palavras vetadas antes de persistir
        return new ComentarioService(new ComentarioRepositorioComModeracao(comentarioRepositorio));
    }

    @Bean
    ComentarioServicoAplicacao comentarioServicoAplicacao(ComentarioService comentarioService) {
        return new ComentarioServicoAplicacao(comentarioService);
    }

    @Bean
    SorteioServico sorteioServico(ISorteioRepositorio sorteioRepositorio,
                                   IEventoRepositorio eventoRepositorio,
                                   INotificacaoServico notificacaoServico) {
        return new SorteioServico(sorteioRepositorio, eventoRepositorio, notificacaoServico);
    }

    @Bean
    @Primary
    EspacoRepositorioProxyCache espacoRepositorioProxy(
            @Qualifier("espacoRepositorioImpl") IEspacoRepositorio espacoRepositorio) {
        return new EspacoRepositorioProxyCache(espacoRepositorio);
    }

    @Bean
    EspacoServico espacoServico(EspacoRepositorioProxyCache proxy) {
        // Proxy (Par 2): cache em memória sobre o repositório real de espaços
        return new EspacoServico(proxy);
    }

    @Bean
    GestaoAmbienteInternoServico gestaoAmbienteInternoServico(ISetorRepositorio setorRepositorio,
                                                               IEspacoRepositorio espacoRepositorio) {
        return new GestaoAmbienteInternoServico(setorRepositorio, espacoRepositorio);
    }

    @Bean
    PreReservaServico preReservaServico(IPreReservaRepositorio preReservaRepositorio,
                                        ISetorRepositorio setorRepositorio) {
        return new PreReservaServico(preReservaRepositorio, setorRepositorio);
    }

    @Bean
    SuporteTecnicoServico suporteTecnicoServico(IChamadoSuporteRepositorio chamadoRepositorio,
                                                 ISetorRepositorio setorRepositorio,
                                                 INotificacaoServico notificacaoServico) {
        return new SuporteTecnicoServico(chamadoRepositorio, setorRepositorio, notificacaoServico);
    }

    @Bean
    EquipamentoServico equipamentoServico(IEquipamentoRepositorio equipamentoRepositorio,
                                           INotificacaoServico notificacaoServico) {
        // Proxy (Par 2): cache em memória sobre o repositório real de equipamentos
        return new EquipamentoServico(
                new EquipamentoRepositorioProxyCache(equipamentoRepositorio),
                notificacaoServico);
    }

    @Bean
    RecursoAcessibilidadeServico recursoAcessibilidadeServico(
            IRecursoAcessibilidadeRepositorio repositorio,
            IEventoRepositorio eventoRepositorio,
            INotificacaoServico notificacaoServico) {
        return new RecursoAcessibilidadeServico(repositorio, eventoRepositorio, notificacaoServico);
    }

    @Bean
    OrcamentoServico orcamentoServico(IOrcamentoRepositorio orcamentoRepositorio,
                                       IDespesaRepositorio despesaRepositorio) {
        return new OrcamentoServico(orcamentoRepositorio, despesaRepositorio);
    }

    @Bean
    BloqueioAdministrativoServico bloqueioAdministrativoServico(
            IBloqueioAdministrativoRepositorio bloqueioRepositorio,
            IEventoRepositorio eventoRepositorio,
            EspacoRepositorioProxyCache proxy,
            EventoBarramento barramento) {
        // Observer (Par 3): publica eventos no barramento em vez de chamar NotificacaoServico direto
        return new BloqueioAdministrativoServico(
                bloqueioRepositorio, eventoRepositorio,
                proxy, barramento);
    }

    @Bean
    BloqueioNotificacaoObservador bloqueioNotificacaoObservador(EventoBarramento barramento,
                                                                 INotificacaoServico notificacaoServico) {
        return new BloqueioNotificacaoObservador(barramento, notificacaoServico);
    }

    @Bean
    IngressoNotificacaoObservador ingressoNotificacaoObservador(EventoBarramento barramento,
                                                                 INotificacaoServico notificacaoServico) {
        return new IngressoNotificacaoObservador(barramento, notificacaoServico);
    }

    @Bean
    AplicarCupomServico aplicarCupomServico(ICupomRepositorio cupomRepositorio) {
        return new AplicarCupomServico(cupomRepositorio);
    }

    @Bean
    IngressoServico ingressoServico(IIngressoRepositorio ingressoRepositorio,
                                     GatewayPagamentoSimulado gatewaySimulado,
                                     EventoBarramento barramento,
                                     AplicarCupomServico cupomServico) {
        // Decorator: GatewayPagamentoComLog envolve o gateway real adicionando logs
        IGatewayPagamento gatewayComLog = new GatewayPagamentoComLog(gatewaySimulado);
        return new IngressoServico(ingressoRepositorio, gatewayComLog, barramento, cupomServico);
    }

    @Bean
    CatracaServico catracaServico(ICatracaRepositorio catracaRepositorio) {
        return new CatracaServico(catracaRepositorio);
    }

    @Bean
    ArtistaServico artistaServico(IArtistaRepositorio artistaRepositorio,
                                   IProdutorRepositorio produtorRepositorio) {
        return new ArtistaServico(artistaRepositorio, produtorRepositorio);
    }

    @Bean
    ProdutorServico produtorServico(IProdutorRepositorio produtorRepositorio,
                                     IArtistaRepositorio artistaRepositorio) {
        return new ProdutorServico(produtorRepositorio, artistaRepositorio);
    }

    @Bean
    PatrocinioServico patrocinioServico(IPatrocinioRepositorio patrocinioRepositorio) {
        // Proxy: PatrocinioRepositorioProxyCache adiciona cache em memória sobre o repositório real
        return new PatrocinioServico(new PatrocinioRepositorioProxyCache(patrocinioRepositorio));
    }

    @Bean
    DesempenhoTeatroServico desempenhoTeatroServico(IOrcamentoRepositorio orcamentoRepositorio,
                                                     IDespesaRepositorio despesaRepositorio,
                                                     IIngressoRepositorio ingressoRepositorio) {
        return new DesempenhoTeatroServico(orcamentoRepositorio, despesaRepositorio, ingressoRepositorio);
    }

    // ── Application Services ───────────────────────────────────────────────

    @Bean
    EventoServicoAplicacao eventoServicoAplicacao(EventoServico eventoServico,
                                                   EventoRepositorioAplicacao repositorio) {
        return new EventoServicoAplicacao(eventoServico, repositorio);
    }

    @Bean
    SorteioServicoAplicacao sorteioServicoAplicacao(SorteioServico sorteioServico,
                                                     SorteioRepositorioAplicacao repositorio) {
        return new SorteioServicoAplicacao(sorteioServico, repositorio);
    }

    @Bean
    BloqueioAdministrativoServicoAplicacao bloqueioAdministrativoServicoAplicacao(
            BloqueioAdministrativoServico servico,
            BloqueioAdministrativoRepositorioAplicacao repositorio,
            IngressoRepositorioAplicacao ingressoRepositorio) {
        return new BloqueioAdministrativoServicoAplicacao(servico, repositorio, ingressoRepositorio);
    }

    @Bean
    IngressoServicoAplicacao ingressoServicoAplicacao(IngressoServico ingressoServico,
                                                       IngressoRepositorioAplicacao repositorio,
                                                       AplicarCupomServico aplicarCupomServico) {
        return new IngressoServicoAplicacao(ingressoServico, repositorio, aplicarCupomServico);
    }

    @Bean
    CupomServicoAplicacao cupomServicoAplicacao(ICupomRepositorio cupomRepositorio) {
        return new CupomServicoAplicacao(cupomRepositorio);
    }

    @Bean
    RecursoAcessibilidadeServicoAplicacao recursoAcessibilidadeServicoAplicacao(
            RecursoAcessibilidadeServico servico,
            IRecursoAcessibilidadeRepositorio repositorio) {
        return new RecursoAcessibilidadeServicoAplicacao(servico, repositorio);
    }

    @Bean
    EquipamentoServicoAplicacao equipamentoServicoAplicacao(EquipamentoServico equipamentoServico,
                                                             IEquipamentoRepositorio equipamentoRepositorio) {
        return new EquipamentoServicoAplicacao(equipamentoServico, equipamentoRepositorio);
    }

    @Bean
    AuditoriaServicoAplicacao auditoriaServicoAplicacao(IAuditoriaRepositorio repositorio) {
        return new AuditoriaServicoAplicacao(repositorio);
    }

    @Bean
    CatracaServicoAplicacao catracaServicoAplicacao(CatracaServico catracaServico,
                                                     EventoBarramento barramento) {
        return new CatracaServicoAplicacao(catracaServico, barramento);
    }

    @Bean
    ArtistaServicoAplicacao artistaServicoAplicacao(ArtistaServico artistaServico,
                                                     ArtistaRepositorioAplicacao repositorio) {
        return new ArtistaServicoAplicacao(artistaServico, repositorio);
    }

    @Bean
    ProdutorServicoAplicacao produtorServicoAplicacao(ProdutorServico produtorServico,
                                                       ProdutorRepositorioAplicacao repositorio) {
        return new ProdutorServicoAplicacao(produtorServico, repositorio);
    }

    @Bean
    PatrocinioServicoAplicacao patrocinioServicoAplicacao(PatrocinioServico patrocinioServico,
                                                           PatrocinioRepositorioAplicacao repositorio,
                                                           IEventoRepositorio eventoRepositorio) {
        return new PatrocinioServicoAplicacao(patrocinioServico, repositorio, eventoRepositorio);
    }

    @Bean
    FinanceiroServicoAplicacao financeiroServicoAplicacao(DesempenhoTeatroServico desempenhoServico,
                                                           FinanceiroRepositorioAplicacao repositorio) {
        return new FinanceiroServicoAplicacao(desempenhoServico, repositorio);
    }
}
