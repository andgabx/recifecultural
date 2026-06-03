package recifecultural.aplicacao.agenda.sorteio;

public interface SorteioInscritoResumo extends SorteioResumo {
    String getSorteioId();
    String getStatusSorteio();
    String getStatusInscricao();
    /** Posição (1-based) entre ganhadores ou suplentes. Null se não aplicável. */
    Integer getPosicao();
    int getTotalInscritos();

    @Override
    default String getId() { return getSorteioId(); }

    @Override
    default String getStatus() { return getStatusSorteio(); }
}
