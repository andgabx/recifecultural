package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/*
 * Padrão Proxy (virtual/cache): intercepta acessos ao repositório real de
 * equipamentos e mantém cache em memória para `obterPorId`. Buscas por
 * disponibilidade/alocação não usam cache porque o resultado depende de
 * múltiplos critérios e muda com frequência.
 */
public class EquipamentoRepositorioProxyCache implements IEquipamentoRepositorio {

    private final IEquipamentoRepositorio delegado;
    private final Map<EquipamentoId, Equipamento> cache = new ConcurrentHashMap<>();

    public EquipamentoRepositorioProxyCache(IEquipamentoRepositorio delegado) {
        if (delegado == null) throw new IllegalArgumentException("Delegado não pode ser nulo.");
        this.delegado = delegado;
    }

    @Override
    public void salvar(Equipamento equipamento) {
        delegado.salvar(equipamento);
        cache.put(equipamento.getId(), equipamento);
    }

    @Override
    public void atualizar(Equipamento equipamento) {
        cache.remove(equipamento.getId());
        delegado.atualizar(equipamento);
    }

    @Override
    public void deletar(EquipamentoId id) {
        cache.remove(id);
        delegado.deletar(id);
    }

    @Override
    public Optional<Equipamento> obterPorId(EquipamentoId id) {
        Equipamento emCache = cache.get(id);
        if (emCache != null) return Optional.of(emCache);
        Optional<Equipamento> doRepo = delegado.obterPorId(id);
        doRepo.ifPresent(e -> cache.put(id, e));
        return doRepo;
    }

    @Override
    public List<Equipamento> buscarDisponiveisPorEspacoENome(EspacoId espacoId, String nome, int quantidade) {
        return delegado.buscarDisponiveisPorEspacoENome(espacoId, nome, quantidade);
    }

    @Override
    public List<Equipamento> listarPorEspaco(EspacoId espacoId) {
        return delegado.listarPorEspaco(espacoId);
    }

    @Override
    public List<Equipamento> buscarAlocadosPorEvento(UUID eventoId) {
        return delegado.buscarAlocadosPorEvento(eventoId);
    }
}
