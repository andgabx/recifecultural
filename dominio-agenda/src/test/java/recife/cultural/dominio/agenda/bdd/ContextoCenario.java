package recife.cultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.EventoRepositorio;
import recifecultural.dominio.agenda.evento.EventoService;

import static org.mockito.Mockito.withSettings;
import static org.mockito.quality.Strictness.STRICT_STUBS;

public class ContextoCenario {
    public Evento evento;
    public Exception excecaoCapturada;
    public EventoRepositorio repositorio = Mockito.mock(
            EventoRepositorio.class,
            withSettings().strictness(STRICT_STUBS)
    );
    public EventoService servico = new EventoService(repositorio);
}
