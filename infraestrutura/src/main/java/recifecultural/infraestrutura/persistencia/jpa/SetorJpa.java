package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.setor.Assento;
import recifecultural.dominio.espaco.setor.ISetorRepositorio;
import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.SetorId;
import recifecultural.dominio.espaco.setor.StatusAssento;
import recifecultural.dominio.espaco.setor.TipoSetor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "setor")
class SetorJpa {
    @Id
    UUID id;
    UUID espacoId;
    String nome;
    @Enumerated(EnumType.STRING)
    TipoSetor tipoSetor;
    int fileirasHorizontais;
    int assentosPorFileiraVertical;
    int versao;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "setor_assento", joinColumns = @JoinColumn(name = "setor_id"))
    List<AssentoJpa> assentos = new ArrayList<>();
}

@Embeddable
class AssentoJpa {
    UUID id;
    String codigo;
    String fileira;
    int numero;
    @Enumerated(EnumType.STRING)
    StatusAssento status;
    @Enumerated(EnumType.STRING)
    MotivoIndisponibilidadeAssento motivoIndisponibilidade;
    int versao;
}

interface SetorJpaRepository extends JpaRepository<SetorJpa, UUID> {
    List<SetorJpa> findByEspacoId(UUID espacoId);
}

@Repository
class SetorRepositorioImpl implements ISetorRepositorio {

    private final SetorJpaRepository jpa;
    private final JpaMapeador mapeador;

    SetorRepositorioImpl(SetorJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Setor setor) {
        jpa.save(mapeador.map(setor, SetorJpa.class));
    }

    @Override
    public void atualizar(Setor setor) {
        jpa.save(mapeador.map(setor, SetorJpa.class));
    }

    @Override
    public Optional<Setor> obterPorId(SetorId id) {
        return jpa.findById(id.valor()).map(s -> mapeador.map(s, Setor.class));
    }

    @Override
    public List<Setor> listarPorEspaco(EspacoId espacoId) {
        return jpa.findByEspacoId(espacoId.valor())
                .stream().map(s -> mapeador.map(s, Setor.class)).toList();
    }
}
