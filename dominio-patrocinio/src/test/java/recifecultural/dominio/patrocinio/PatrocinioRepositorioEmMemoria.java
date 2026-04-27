package recifecultural.dominio.patrocinio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PatrocinioRepositorioEmMemoria implements IPatrocinioRepositorio {

    private final Map<PatrocinioId, Patrocinio> store = new HashMap<>();

    @Override
    public void salvar(Patrocinio patrocinio) {
        store.put(patrocinio.getId(), patrocinio);
    }

    @Override
    public Patrocinio buscarPorId(PatrocinioId id) {
        return store.get(id);
    }

    @Override
    public List<Patrocinio> buscarPorEvento(EventoId eventoId) {
        List<Patrocinio> result = new ArrayList<>();
        for (Patrocinio p : store.values()) {
            if (p.getEventoId().equals(eventoId)) {
                result.add(p);
            }
        }
        return result;
    }

    @Override
    public Optional<Patrocinio> buscarMasterPorEvento(EventoId eventoId) {
        return store.values().stream()
                .filter(p -> p.getEventoId().equals(eventoId)
                        && p.getTipo() == TipoPatrocinio.MASTER
                        && p.getStatus() != StatusPatrocinio.CANCELADO_EVENTO
                        && p.getStatus() != StatusPatrocinio.CANCELADO_PATROCINADOR)
                .findFirst();
    }

    @Override
    public Optional<Patrocinio> buscarPorEventoECategoria(EventoId eventoId, String categoria) {
        return store.values().stream()
                .filter(p -> p.getEventoId().equals(eventoId)
                        && p.getCategoriaPatrocinio().equalsIgnoreCase(categoria)
                        && p.getStatus() != StatusPatrocinio.CANCELADO_EVENTO
                        && p.getStatus() != StatusPatrocinio.CANCELADO_PATROCINADOR)
                .findFirst();
    }

    @Override
    public void atualizar(Patrocinio patrocinio) {
        store.put(patrocinio.getId(), patrocinio);
    }
}
