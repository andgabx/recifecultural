package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.util.List;

public interface IBloqueioAdministrativoRepositorio {
    boolean salvar(BloqueioAdministrativo bloqueioAdministrativo);
    BloqueioAdministrativo obter(BloqueioAdministrativoId id);
    List<BloqueioAdministrativo> obterTodos();
}