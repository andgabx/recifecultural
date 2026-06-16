package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoCanceladoPorBloqueioEvento;
import recifecultural.dominio.compartilhado.notificacao.IUsuarioContextoServico;
import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoServico;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.agenda.equipamento.AlocacaoRiderTecnicoServico;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.agenda.equipamento.EquipamentoServico;

import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoServico;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoRepositorio;

import java.util.UUID;

public class ContextoCenario {
    public Exception excecaoCapturada;

    // Evento
    public Evento evento;
    public IEventoRepositorio repositorioEvento = Mockito.mock(IEventoRepositorio.class);
    public EventoServico servicoEvento = new EventoServico(repositorioEvento);

    // Notificação
    public UUID idUsuarioAtual;
    public Notificacao notificacaoAtual;
    public INotificacaoRepositorio repositorioNotificacao = Mockito.mock(INotificacaoRepositorio.class);
    public IUsuarioContextoServico usuarioContextoServico = Mockito.mock(IUsuarioContextoServico.class);
    public NotificacaoServico servicoNotificacao = Mockito.spy(new NotificacaoServico(repositorioNotificacao, usuarioContextoServico));

    // Espaço
    public Espaco espaco;
    public EspacoId idEspacoAtual;
    public IEspacoRepositorio repositorioEspaco = Mockito.mock(IEspacoRepositorio.class);
    public EspacoServico servicoEspaco = new EspacoServico(repositorioEspaco);

    // Bloqueio Administrativo
    public UUID idLocalAtual;
    public IBloqueioAdministrativoRepositorio repositorioBloqueio = Mockito.mock(IBloqueioAdministrativoRepositorio.class);
    public final EventoBarramento barramento;
    public final BloqueioAdministrativoServico servicoBloqueio;

    // Equipamentos
    public IEquipamentoRepositorio repositorioEquipamento = Mockito.mock(IEquipamentoRepositorio.class);
    public EquipamentoId idEquipamentoAtual;
    public EquipamentoServico servicoEquipamento = new EquipamentoServico(repositorioEquipamento, servicoNotificacao, repositorioEvento);
    public AlocacaoRiderTecnicoServico servicoAlocacao = new AlocacaoRiderTecnicoServico(repositorioEquipamento, repositorioEvento, servicoNotificacao);

    public ContextoCenario() {
        BarramentoEmMemoria b = new BarramentoEmMemoria();
        // Reproduz o BloqueioNotificacaoObservador da camada de aplicação:
        // assina o barramento e dispara notificações ao receber o evento de cancelamento.
        b.adicionar((EventoCanceladoPorBloqueioEvento evt) -> {
            String mensagem = String.format(
                    "Atenção: O evento '%s' foi cancelado por motivos técnicos. Justificativa: %s",
                    evt.tituloEvento(), evt.justificativaBloqueio());
            servicoNotificacao.enviarNotificacao(evt.promotorId(), mensagem, "EVENTO_CANCELADO", null);
            servicoNotificacao.enviarBroadcast(mensagem, "PARTICIPANTES_EVENTO_CANCELADO", null);
        });
        this.barramento = b;
        this.servicoBloqueio = new BloqueioAdministrativoServico(
                repositorioBloqueio, repositorioEvento, repositorioEspaco, barramento);
    }
}
