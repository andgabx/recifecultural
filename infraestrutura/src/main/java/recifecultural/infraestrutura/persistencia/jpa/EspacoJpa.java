package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.Ocupacao;
import recifecultural.dominio.espaco.espaco.StatusEspaco;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "espaco")
class EspacoJpa {
    @Id
    UUID id;
    String nome;
    int capacidadeMaxima;
    @Enumerated(EnumType.STRING)
    StatusEspaco status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "espaco_rider_tecnico", joinColumns = @JoinColumn(name = "espaco_id"))
    @Column(name = "item")
    List<String> riderTecnico = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "espaco_ocupacao", joinColumns = @JoinColumn(name = "espaco_id"))
    List<OcupacaoJpa> ocupacoes = new ArrayList<>();
}

@Embeddable
class OcupacaoJpa {
    LocalDateTime inicio;
    LocalDateTime fim;
    int minutosMontagem;
    int minutosDesmontagem;
    int bufferExtra;
}

interface EspacoJpaRepository extends JpaRepository<EspacoJpa, UUID> {
}

@Repository
class EspacoRepositorioImpl implements IEspacoRepositorio {

    private final EspacoJpaRepository jpa;
    private final JpaMapeador mapeador;

    EspacoRepositorioImpl(EspacoJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Espaco espaco) {
        jpa.save(mapeador.map(espaco, EspacoJpa.class));
    }

    @Override
    public void atualizar(Espaco espaco) {
        jpa.save(mapeador.map(espaco, EspacoJpa.class));
    }

    @Override
    public Optional<Espaco> obterPorId(EspacoId id) {
        return jpa.findById(id.valor()).map(e -> mapeador.map(e, Espaco.class));
    }

    @Override
    public List<Espaco> listarTodos() {
        return jpa.findAll().stream()
                .map(e -> mapeador.map(e, Espaco.class))
                .toList();
    }

    @Override
    public List<Ocupacao> buscarOcupacoesPorPeriodo(EspacoId id, LocalDateTime inicio, LocalDateTime fim) {
        return jpa.findById(id.valor())
                .map(e -> e.ocupacoes.stream()
                        .filter(o -> o.inicio != null && o.fim != null)
                        .map(o -> new Ocupacao(o.inicio, o.fim, o.minutosMontagem, o.minutosDesmontagem, o.bufferExtra))
                        .filter(o -> o.inicioEfetivo().isBefore(fim) && o.fimEfetivo().isAfter(inicio))
                        .toList())
                .orElse(List.of());
    }

    @Override
    public void salvarOcupacao(EspacoId id, Ocupacao ocupacao) {
        jpa.findById(id.valor()).ifPresent(e -> {
            var jpaOcupacao = new OcupacaoJpa();
            jpaOcupacao.inicio = ocupacao.inicio();
            jpaOcupacao.fim = ocupacao.fim();
            jpaOcupacao.minutosMontagem = ocupacao.minutosMontagem();
            jpaOcupacao.minutosDesmontagem = ocupacao.minutosDesmontagem();
            jpaOcupacao.bufferExtra = ocupacao.bufferExtra();
            e.ocupacoes.add(jpaOcupacao);
            jpa.save(e);
        });
    }
}
