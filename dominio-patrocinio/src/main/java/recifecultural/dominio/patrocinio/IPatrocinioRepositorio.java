package recifecultural.dominio.patrocinio;

import java.util.List;
import java.util.Optional;

public interface IPatrocinioRepositorio {
    void salvar(Patrocinio patrocinio);
    Patrocinio buscarPorId(PatrocinioId id);
    List<Patrocinio> buscarPorEvento(EventoId eventoId);
    Optional<Patrocinio> buscarMasterPorEvento(EventoId eventoId);
    Optional<Patrocinio> buscarPorEventoECategoria(EventoId eventoId, String categoria);
    void atualizar(Patrocinio patrocinio);
}
