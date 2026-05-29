package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.IPatrocinioRepositorio;
import recifecultural.dominio.patrocinio.Patrocinio;
import recifecultural.dominio.patrocinio.PatrocinioId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Padrão Proxy (virtual/cache): intercepta acessos ao repositório real de
 * patrocínios e mantém cache em memória para leituras repetidas. Invalida
 * o cache em operações de escrita, garantindo consistência eventual.
 */
public class PatrocinioRepositorioProxyCache implements IPatrocinioRepositorio {

    private final IPatrocinioRepositorio delegado;
    private final Map<PatrocinioId, Patrocinio> cache = new ConcurrentHashMap<>();

    public PatrocinioRepositorioProxyCache(IPatrocinioRepositorio delegado) {
        if (delegado == null) throw new IllegalArgumentException("Delegado não pode ser nulo.");
        this.delegado = delegado;
    }

    @Override
    public Patrocinio buscarPorId(PatrocinioId id) {
        return cache.computeIfAbsent(id, delegado::buscarPorId);
    }

    @Override
    public void salvar(Patrocinio patrocinio) {
        delegado.salvar(patrocinio);
        cache.put(patrocinio.getId(), patrocinio);
    }

    @Override
    public void atualizar(Patrocinio patrocinio) {
        cache.remove(patrocinio.getId());
        delegado.atualizar(patrocinio);
    }

    @Override
    public Optional<Patrocinio> buscarMasterPorEvento(EventoId eventoId) {
        return delegado.buscarMasterPorEvento(eventoId);
    }

    @Override
    public Optional<Patrocinio> buscarPorEventoECategoria(EventoId eventoId, String categoria) {
        return delegado.buscarPorEventoECategoria(eventoId, categoria);
    }

    @Override
    public List<Patrocinio> buscarPorEvento(EventoId eventoId) {
        return delegado.buscarPorEvento(eventoId);
    }
}
