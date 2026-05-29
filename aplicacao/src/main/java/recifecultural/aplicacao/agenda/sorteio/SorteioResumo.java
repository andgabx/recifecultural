package recifecultural.aplicacao.agenda.sorteio;

public interface SorteioResumo {
    String getId();
    String getEventoId();
    String getApresentacaoId();
    int getVagas();
    String getStatus();
    String getPrazoInscricao();
    String getDataApresentacao();
}
