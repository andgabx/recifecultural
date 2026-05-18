package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.catraca.ICatracaRepositorio;
import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.IngressoCatracaId;
import recifecultural.dominio.catraca.StatusIngressoCatraca;
import recifecultural.dominio.catraca.TipoIngresso;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingresso_catraca")
class IngressoCatracaJpa {
    @Id
    String id;
    String idEvento;
    @Enumerated(EnumType.STRING)
    StatusIngressoCatraca status;
    LocalDateTime horarioInicioEvento;
    @Enumerated(EnumType.STRING)
    TipoIngresso tipoIngresso;
    String portaoAcesso;
}

interface IngressoCatracaJpaRepository extends JpaRepository<IngressoCatracaJpa, String> {
}

@Repository
class CatracaRepositorioImpl implements ICatracaRepositorio {

    private final IngressoCatracaJpaRepository jpa;
    private final JpaMapeador mapeador;

    CatracaRepositorioImpl(IngressoCatracaJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public IngressoCatraca buscarPorId(String idIngresso) {
        return jpa.findById(idIngresso).map(i -> mapeador.map(i, IngressoCatraca.class)).orElse(null);
    }

    @Override
    public void salvar(IngressoCatraca ingresso) {
        jpa.save(mapeador.map(ingresso, IngressoCatracaJpa.class));
    }
}
