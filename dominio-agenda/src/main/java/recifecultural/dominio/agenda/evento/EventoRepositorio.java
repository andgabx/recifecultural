package recifecultural.dominio.agenda.evento;

import recifecultural.dominio.agenda.evento.Evento;

import java.util.Optional;
import java.util.UUID;

public interface EventoRepositorio {
    void salvar(Evento evento);
    Optional<Evento> obter(UUID id);
    void atualizar(Evento evento);
    void deletar(UUID id);
}
