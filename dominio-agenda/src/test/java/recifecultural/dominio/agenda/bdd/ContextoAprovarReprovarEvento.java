package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;

public class ContextoAprovarReprovarEvento {
    public Exception excecaoCapturada;

    public Evento evento;
    public IEventoRepositorio repositorioEvento = Mockito.mock(IEventoRepositorio.class);
    public EventoServico servicoEvento = new EventoServico(repositorioEvento);
}
