package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.agenda.equipamento.StatusEquipamento;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "equipamento")
class EquipamentoJpa {
    @Id
    UUID id;
    UUID espacoId;
    String nome;
    @Enumerated(EnumType.STRING)
    StatusEquipamento status;
    UUID eventoAlocadoId;
}

interface EquipamentoJpaRepository extends JpaRepository<EquipamentoJpa, UUID> {
    @Query("SELECT e FROM EquipamentoJpa e WHERE e.espacoId = :espacoId AND e.nome = :nome AND e.status = 'DISPONIVEL'")
    List<EquipamentoJpa> findDisponiveisPorEspacoENome(UUID espacoId, String nome);

    List<EquipamentoJpa> findByEspacoId(UUID espacoId);

    List<EquipamentoJpa> findByEventoAlocadoId(UUID eventoId);
}

@Repository
class EquipamentoRepositorioImpl implements IEquipamentoRepositorio {

    private final EquipamentoJpaRepository jpa;
    private final JpaMapeador mapeador;

    EquipamentoRepositorioImpl(EquipamentoJpaRepository jpa, JpaMapeador mapeador) {
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
