package recife.cultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.evento.EventoRepositorio;
import recifecultural.dominio.agenda.evento.EventoService;
import recifecultural.dominio.agenda.evento.Evento;

public class ContextoCenario {
    public Evento evento;
    public Exception excecaoCapturada;
    public EventoRepositorio repositorio = Mockito.mock(EventoRepositorio.class);
    public EventoService servico = new EventoService(repositorio);
}
