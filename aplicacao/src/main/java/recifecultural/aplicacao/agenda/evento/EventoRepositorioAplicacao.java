package recifecultural.aplicacao.agenda.evento;

import java.util.List;
import java.util.UUID;

public interface EventoRepositorioAplicacao {
    List<EventoResumo> pesquisarResumos();
    EventoResumoExpandido buscarResumoExpandido(UUID id);
    List<EventoResumo> listarPorPromotor(UUID promotorId);
}
