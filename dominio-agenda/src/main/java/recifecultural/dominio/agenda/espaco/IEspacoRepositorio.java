package recifecultural.dominio.agenda.espaco;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;
import recifecultural.dominio.agenda.espaco.EspacoId;

public interface IEspacoRepositorio {
    void salvar(Espaco espaco);
    void atualizar(Espaco espaco);
    Optional<Espaco> obterPorId(EspacoId id);
    List<Ocupacao> buscarOcupacoesPorPeriodo(EspacoId id, LocalDateTime inicio, LocalDateTime fim);
    void salvarOcupacao(EspacoId id, Ocupacao ocupacao);
}