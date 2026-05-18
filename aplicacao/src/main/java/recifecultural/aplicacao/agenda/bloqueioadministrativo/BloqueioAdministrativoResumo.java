package recifecultural.aplicacao.agenda.bloqueioadministrativo;

public interface BloqueioAdministrativoResumo {
    String getId();
    String getEspacoId();
    String getDataInicio();
    String getDataFim();
    String getJustificativa();
    boolean isAtivo();
}
