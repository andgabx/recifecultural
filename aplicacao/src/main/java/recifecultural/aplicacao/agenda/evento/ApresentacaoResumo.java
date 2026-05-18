package recifecultural.aplicacao.agenda.evento;

/**
 * Cada data de apresentação do evento exposta com um ID determinístico
 * derivado de (eventoId, dataIso). Permite que sorteio e acessibilidade
 * marquem a MESMA apresentação sem o usuário digitar UUID à mão.
 */
public interface ApresentacaoResumo {
    String getId();
    String getEventoId();
    String getDataHora();
}
