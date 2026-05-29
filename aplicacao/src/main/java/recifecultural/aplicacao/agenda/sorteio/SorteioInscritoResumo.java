package recifecultural.aplicacao.agenda.sorteio;

public interface SorteioInscritoResumo {
    String getSorteioId();
    String getEventoId();
    String getApresentacaoId();
    int getVagas();
    String getStatusSorteio();
    String getPrazoInscricao();
    String getDataApresentacao();
    String getStatusInscricao();
    /** Posição (1-based) entre ganhadores ou suplentes. Null se não aplicável. */
    Integer getPosicao();
    int getTotalInscritos();
}
