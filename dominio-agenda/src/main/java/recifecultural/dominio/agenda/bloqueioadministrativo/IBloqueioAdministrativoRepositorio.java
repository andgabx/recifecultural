package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.util.List;

public interface IBloqueioAdministrativoRepositorio {
    boolean salvar(BloqueioAdministrativo bloqueioAdministrativo);
    boolean atualizar(BloqueioAdministrativo bloqueioAdministrativo);
    boolean deletar(BloqueioAdministrativoId id);
    BloqueioAdministrativo obter(BloqueioAdministrativoId id);
    List<BloqueioAdministrativo> obterTodos();
}