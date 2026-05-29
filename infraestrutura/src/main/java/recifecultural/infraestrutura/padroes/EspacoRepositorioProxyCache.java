package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.Ocupacao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Padrão Proxy (virtual/cache): intercepta acessos ao repositório real de
 * espaços e mantém cache em memória para `obterPorId`, que é a consulta
 * mais frequente do dashboard administrativo. Invalida o cache em escritas.
 */
public class EspacoRepositorioProxyCache implements IEspacoRepositorio {

    private final IEspacoRepositorio delegado;
    private final Map<EspacoId, Espaco> cache = new ConcurrentHashMap<>();

    public EspacoRepositorioProxyCache(IEspacoRepositorio delegado) {
        if (delegado == null) throw new IllegalArgumentException("Delegado não pode ser nulo.");
        this.delegado = delegado;
    }

    @Override
    public void salvar(Espaco espaco) {
        delegado.salvar(espaco);
        cache.put(espaco.getId(), espaco);
    }

    @Override
    public void atualizar(Espaco espaco) {
        cache.remove(espaco.getId());
        delegado.atualizar(espaco);
    }

    @Override
    public Optional<Espaco> obterPorId(EspacoId id) {
        Espaco emCache = cache.get(id);
        if (emCache != null) return Optional.of(emCache);
        Optional<Espaco> doRepo = delegado.obterPorId(id);
        doRepo.ifPresent(e -> cache.put(id, e));
        return doRepo;
    }

    @Override
    public List<Espaco> listarTodos() {
        return delegado.listarTodos();
    }

    @Override
    public List<Ocupacao> buscarOcupacoesPorPeriodo(EspacoId id, LocalDateTime inicio, LocalDateTime fim) {
        return delegado.buscarOcupacoesPorPeriodo(id, inicio, fim);
    }

    @Override
    public void salvarOcupacao(EspacoId id, Ocupacao ocupacao) {
        delegado.salvarOcupacao(id, ocupacao);
    }
}
