package recifecultural.dominio.agenda.comentario;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComentarioRepositorio {
    void salvar(Comentario comentario);
    Optional<Comentario> obter(UUID id);
    void atualizar(Comentario comentario);
    void deletar(UUID id);
    List<Comentario> listarPorEvento(UUID eventoId);
}
