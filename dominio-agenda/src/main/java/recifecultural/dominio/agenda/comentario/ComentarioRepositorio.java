package recifecultural.dominio.agenda.comentario;

import recifecultural.dominio.agenda.evento.EventoId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComentarioRepositorio {
    void salvar(Comentario comentario);
    Optional<Comentario> obter(ComentarioId id);
    void atualizar(Comentario comentario);
    void deletar(ComentarioId id);
    List<Comentario> listarPorEvento(EventoId eventoId);
}
