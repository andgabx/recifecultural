package recifecultural.dominio.agenda.espaco;

import java.util.Optional;
import recifecultural.dominio.agenda.espaco.EspacoId;

public interface IEspacoRepositorio {
    void salvar(Espaco espaco);
    void atualizar(Espaco espaco);
    Optional<Espaco> obterPorId(EspacoId id);
}