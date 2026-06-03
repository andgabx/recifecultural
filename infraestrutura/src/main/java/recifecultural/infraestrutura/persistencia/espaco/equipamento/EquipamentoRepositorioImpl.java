package recifecultural.infraestrutura.persistencia.espaco.equipamento;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class EquipamentoRepositorioImpl implements IEquipamentoRepositorio {

    private final EquipamentoJpaRepository jpa;
    private final ModelMapper mapeador;

    public EquipamentoRepositorioImpl(EquipamentoJpaRepository jpa, ModelMapper mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Equipamento equipamento) {
        jpa.save(mapeador.map(equipamento, EquipamentoJpa.class));
    }

    @Override
    public void atualizar(Equipamento equipamento) {
        jpa.save(mapeador.map(equipamento, EquipamentoJpa.class));
    }

    @Override
    public void deletar(EquipamentoId id) {
        jpa.deleteById(id.valor());
    }

    @Override
    public Optional<Equipamento> obterPorId(EquipamentoId id) {
        return jpa.findById(id.valor()).map(e -> mapeador.map(e, Equipamento.class));
    }

    @Override
    public List<Equipamento> buscarDisponiveisPorEspacoENome(EspacoId espacoId, String nome, int quantidade) {
        return jpa.findDisponiveisPorEspacoENome(espacoId.valor(), nome).stream()
                .limit(quantidade)
                .map(e -> mapeador.map(e, Equipamento.class))
                .toList();
    }

    @Override
    public List<Equipamento> listarPorEspaco(EspacoId espacoId) {
        return jpa.findByEspacoId(espacoId.valor()).stream()
                .map(e -> mapeador.map(e, Equipamento.class))
                .toList();
    }

    @Override
    public List<Equipamento> buscarAlocadosPorEvento(UUID eventoId) {
        return jpa.findByEventoAlocadoId(eventoId).stream()
                .map(e -> mapeador.map(e, Equipamento.class))
                .toList();
    }
}
