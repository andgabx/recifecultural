package recifecultural.dominio.agenda.bloqueioadministrativo;

import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.List;

public interface IBloqueioAdministrativoRepositorio {
    void salvar(BloqueioAdministrativo bloqueio);
    BloqueioAdministrativo obter(BloqueioAdministrativoId id);
    void atualizar(BloqueioAdministrativo bloqueio);
    void deletar(BloqueioAdministrativoId id);
    List<BloqueioAdministrativo> obterTodos();
    List<BloqueioAdministrativo> buscarPorEspaco(EspacoId espacoId);
}