package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.EventoServico;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;

public class ContextoAprovarReprovarEvento {
    public Exception excecaoCapturada;

    public Evento evento;
    public IEventoRepositorio repositorioEvento;
    public EventoServico servicoEvento;

    public ContextoAprovarReprovarEvento() {
        repositorioEvento = Mockito.mock(IEventoRepositorio.class);
        Mockito.when(repositorioEvento.obterReprovacoesPorPromotor(any())).thenReturn(List.of());
        Mockito.when(repositorioEvento.obterEventosFinalizadosPorPromotor(any())).thenReturn(List.of());
        Mockito.when(repositorioEvento.obterEventosAprovadosPorPromotor(any())).thenReturn(List.of());
        Mockito.when(repositorioEvento.obterPorLocalEIntervalo(any(), any(), any())).thenReturn(List.of());
        servicoEvento = new EventoServico(repositorioEvento);
    }
}
