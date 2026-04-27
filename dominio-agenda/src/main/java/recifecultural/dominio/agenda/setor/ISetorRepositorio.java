package recifecultural.dominio.agenda.setor;

import recifecultural.dominio.agenda.espaco.EspacoId;
import java.util.List;
import java.util.Optional;

public interface ISetorRepositorio {
    void salvar(Setor setor);
    void atualizar(Setor setor);
    Optional<Setor> obterPorId(SetorId id);
    List<Setor> listarPorEspaco(EspacoId espacoId);
}