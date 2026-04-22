package recifecultural.dominio.agenda.evento;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEventoRepositorio {

    void salvar(Evento evento);
    void atualizar(Evento evento);
    void deletar(UUID id);

    Optional<Evento> obter(UUID id);
    List<Evento> obterTodos();
    List<Evento> obterPorLocalEIntervalo(UUID localId, LocalDateTime inicio, LocalDateTime fim);
}