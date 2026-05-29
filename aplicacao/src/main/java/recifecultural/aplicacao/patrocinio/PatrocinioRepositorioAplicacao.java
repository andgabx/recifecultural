package recifecultural.aplicacao.patrocinio;

import java.util.List;
import java.util.UUID;

public interface PatrocinioRepositorioAplicacao {
    List<PatrocinioResumo> pesquisarPorEvento(UUID eventoId);
}
