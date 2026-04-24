package recifecultural.dominio.agenda.equipamento;

import recifecultural.dominio.agenda.espaco.EspacoId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IEquipamentoRepositorio {
    void salvar(Equipamento equipamento);
    void atualizar(Equipamento equipamento);
    void deletar(EquipamentoId id);

    Optional<Equipamento> obterPorId(EquipamentoId id);
    List<Equipamento> buscarDisponiveisPorEspacoENome(EspacoId espacoId, String nome, int quantidade);
    List<Equipamento> buscarAlocadosPorEvento(UUID eventoId);
}