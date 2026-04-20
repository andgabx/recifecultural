package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.agenda.bloqueioadministrativo.IBloqueioAdministrativoRepositorio;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.Evento;

import java.util.UUID;

public class ContextoCenario {
    public Exception excecaoCapturada;

    public Evento evento;
    public IEventoRepositorio repositorioEvento = Mockito.mock(IEventoRepositorio.class);
    public EventoServico servicoEvento = new EventoServico(repositorioEvento);

    public UUID idLocalAtual;
    public IBloqueioAdministrativoRepositorio repositorioBloqueio = Mockito.mock(IBloqueioAdministrativoRepositorio.class);
    public BloqueioAdministrativoServico servicoBloqueio = new BloqueioAdministrativoServico(repositorioBloqueio, repositorioEvento);
}