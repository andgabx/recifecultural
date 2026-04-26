package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.BilheteriaDigital;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;
import recifecultural.dominio.agenda.comentario.ComentarioService;
import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.espectador.Espectador;
import recifecultural.dominio.agenda.evento.Evento;

public class ContextoDiscutirEvento {
    public Exception excecaoCapturada;

    public Evento evento;
    public Espectador espectador;
    public Comentario comentario;

    public ComentarioRepositorio comentarioRepositorio = Mockito.mock(ComentarioRepositorio.class);
    public ComentarioService comentarioServico = new ComentarioService(comentarioRepositorio);
    public BilheteriaDigital bilheteria = Mockito.mock(BilheteriaDigital.class);
}
