package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.util.List;

public interface IBloqueioAdministrativoRepositorio {
    void salvar(BloqueioAdministrativo bloqueio);
    BloqueioAdministrativo obter(BloqueioAdministrativoId id);
    void atualizar(BloqueioAdministrativo bloqueio);
    void deletar(BloqueioAdministrativoId id);
    List<BloqueioAdministrativo> obterTodos();
}