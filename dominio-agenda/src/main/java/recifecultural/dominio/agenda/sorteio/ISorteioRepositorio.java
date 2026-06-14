package recifecultural.dominio.agenda.sorteio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ISorteioRepositorio {
    void salvar(Sorteio sorteio);
    Optional<Sorteio> obter(UUID id);
    void atualizar(Sorteio sorteio);
    void deletar(UUID id);

    default boolean existe(UUID id) {
        return obter(id).isPresent();
    }

    List<Inscricao> listarInscricoesOrdenadas(UUID sorteioId, int pagina, int tamanhoPagina);
}
