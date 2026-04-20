package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.util.List;
import java.util.UUID;

public interface IBloqueioAdministrativoRepositorio {
    boolean salvar(BloqueioAdministrativo bloqueioAdministrativo);
    BloqueioAdministrativo obter(UUID id);
    List<BloqueioAdministrativo> obterTodos();
}
