package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.suporte.ChamadoSuporte;
import recifecultural.dominio.espaco.suporte.IChamadoSuporteRepositorio;
import recifecultural.dominio.espaco.suporte.StatusChamado;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "chamado_suporte")
class ChamadoSuporteJpa {
    @Id
    UUID id;
    UUID assentoId;
    @Column(length = 1000)
    String descricao;
    @Enumerated(EnumType.STRING)
    MotivoIndisponibilidadeAssento motivo;
    @Enumerated(EnumType.STRING)
    StatusChamado status;
    LocalDateTime dataAbertura;
}

interface ChamadoSuporteJpaRepository extends JpaRepository<ChamadoSuporteJpa, UUID> {
    @Query("SELECT c FROM ChamadoSuporteJpa c WHERE c.status IN ('ABERTO','EM_ANDAMENTO') AND c.dataAbertura < :limite")
    List<ChamadoSuporteJpa> findAbertosAntesDe(LocalDateTime limite);
}

@Repository
class ChamadoSuporteRepositorioImpl implements IChamadoSuporteRepositorio {

    private final ChamadoSuporteJpaRepository jpa;
    private final JpaMapeador mapeador;

    ChamadoSuporteRepositorioImpl(ChamadoSuporteJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(ChamadoSuporte chamado) {
        jpa.save(mapeador.map(chamado, ChamadoSuporteJpa.class));
    }

    @Override
    public void atualizar(ChamadoSuporte chamado) {
        jpa.save(mapeador.map(chamado, ChamadoSuporteJpa.class));
    }

    @Override
    public Optional<ChamadoSuporte> obterPorId(UUID id) {
        return jpa.findById(id).map(c -> mapeador.map(c, ChamadoSuporte.class));
    }

    @Override
    public List<ChamadoSuporte> listarAbertosAntesDe(LocalDateTime limite) {
        return jpa.findAbertosAntesDe(limite).stream()
                .map(c -> mapeador.map(c, ChamadoSuporte.class))
                .toList();
    }
}
