package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.espaco.Espaco;
import recifecultural.dominio.agenda.espaco.EspacoServico;
import recifecultural.dominio.agenda.espaco.IEspacoRepositorio;
import recifecultural.dominio.agenda.espaco.EspacoId;

import java.util.UUID;

public class ContextoCenario {
    public Exception excecaoCapturada;

    public Evento evento;
    public IEventoRepositorio repositorioEvento = Mockito.mock(IEventoRepositorio.class);
    public EventoServico servicoEvento = new EventoServico(repositorioEvento);

    public UUID idLocalAtual;
    public IBloqueioAdministrativoRepositorio repositorioBloqueio = Mockito.mock(IBloqueioAdministrativoRepositorio.class);
    public BloqueioAdministrativoServico servicoBloqueio = new BloqueioAdministrativoServico(repositorioBloqueio, repositorioEvento);

    public Espaco espaco;
    public EspacoId idEspacoAtual;
    public IEspacoRepositorio repositorioEspaco = Mockito.mock(IEspacoRepositorio.class);
    public EspacoServico servicoEspaco = new EspacoServico(repositorioEspaco);
}