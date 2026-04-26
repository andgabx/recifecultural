package recifecultural.dominio.agenda.sorteio;

import java.util.Optional;
import java.util.UUID;

public interface ISorteioRepositorio {
    void salvar(Sorteio sorteio);
    Optional<Sorteio> obter(UUID id);
    void atualizar(Sorteio sorteio);
    void deletar(UUID id);
}
