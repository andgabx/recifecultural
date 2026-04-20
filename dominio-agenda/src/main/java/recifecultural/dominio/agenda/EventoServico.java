package recifecultural.dominio.agenda;

import java.time.LocalDateTime;
import java.util.List;

public class EventoServico {

    IEventoRepositorio eventoRepositorio;

    EventoServico (IEventoRepositorio eventoRepositorio) {
        this.eventoRepositorio = eventoRepositorio;
    }

    List<Evento> getEventosNoIntervalo(LocalDateTime inicio, LocalDateTime fim) {
        return eventoRepositorio.getEventosNoIntervalo(inicio, fim);
    }
}
