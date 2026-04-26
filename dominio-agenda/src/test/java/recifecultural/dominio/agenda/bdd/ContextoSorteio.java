package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.sorteio.ISorteioRepositorio;
import recifecultural.dominio.agenda.sorteio.Sorteio;
import recifecultural.dominio.agenda.sorteio.SorteioServico;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContextoSorteio {
    public Evento evento;
    public Sorteio sorteio;
    public Exception excecaoCapturada;
    public Map<String, UUID> espectadores = new HashMap<>();

    public IEventoRepositorio eventoRepositorio = Mockito.mock(IEventoRepositorio.class);
    public ISorteioRepositorio sorteioRepositorio = Mockito.mock(ISorteioRepositorio.class);
    public SorteioServico servico = new SorteioServico(sorteioRepositorio, eventoRepositorio);
}
