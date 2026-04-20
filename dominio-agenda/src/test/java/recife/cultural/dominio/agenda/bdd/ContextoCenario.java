package recife.cultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.BilheteriaDigital;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;
import recifecultural.dominio.agenda.comentario.ComentarioService;
import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.espectador.Espectador;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.EventoRepositorio;
import recifecultural.dominio.agenda.evento.EventoService;

public class ContextoCenario {
    public Evento evento;
    public Espectador espectador;
    public Comentario comentario;
    public Exception excecaoCapturada;

    public EventoRepositorio eventoRepositorio = Mockito.mock(EventoRepositorio.class);
    public EventoService eventoServico = new EventoService(eventoRepositorio);

    public ComentarioRepositorio comentarioRepositorio = Mockito.mock(ComentarioRepositorio.class);
    public ComentarioService comentarioServico = new ComentarioService(comentarioRepositorio);

    public BilheteriaDigital bilheteria = Mockito.mock(BilheteriaDigital.class);
}
