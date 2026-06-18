package recifecultural.aplicacao.ingressos;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IngressoRepositorioAplicacao {
    List<IngressoResumo> pesquisarPorEvento(UUID eventoId);
    List<IngressoResumo> listarTodos();
    Set<UUID> buscarAssentosOcupadosPorEvento(UUID eventoId);
}
