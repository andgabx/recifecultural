package recifecultural.dominio.agenda;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface IEventoRepositorio {
    List<Evento> getEventosNoIntervalo(LocalDateTime inicio, LocalDateTime fim);

    List<Evento> getEventosPorLocalEIntervalo(UUID localId, LocalDateTime inicio, LocalDateTime fim);

    void salvar(Evento evento);
}