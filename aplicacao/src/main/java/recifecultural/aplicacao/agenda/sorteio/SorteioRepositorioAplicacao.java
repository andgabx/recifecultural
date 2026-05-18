package recifecultural.aplicacao.agenda.sorteio;

import java.util.List;
import java.util.UUID;

public interface SorteioRepositorioAplicacao {
    List<SorteioResumo> pesquisarPorEvento(UUID eventoId);
    List<SorteioInscritoResumo> pesquisarPorEspectador(UUID espectadorId);
}
