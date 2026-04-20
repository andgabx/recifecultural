package recifecultural.dominio.agenda.evento;

import java.util.Optional;

public interface EventoRepositorio {
    void salvar(Evento evento);
    Optional<Evento> obter(EventoId id);
    void atualizar(Evento evento);
    void deletar(EventoId id);
}
