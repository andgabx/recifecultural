package recifecultural.dominio.agenda.acessibilidade;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IRecursoAcessibilidadeRepositorio {
    void salvar(RecursoAcessibilidade recurso);
    Optional<RecursoAcessibilidade> obter(UUID id);
    List<RecursoAcessibilidade> listarPorApresentacao(UUID apresentacaoId);
    void atualizar(RecursoAcessibilidade recurso);
}
