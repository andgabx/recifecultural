package recifecultural.aplicacao.agenda.bloqueioadministrativo;

import java.util.List;

public interface BloqueioAdministrativoResumo {
    String getId();
    String getEspacoId();
    String getDataInicio();
    String getDataFim();
    String getJustificativa();
    boolean isAtivo();
    List<String> getEventosCancelados();
}
