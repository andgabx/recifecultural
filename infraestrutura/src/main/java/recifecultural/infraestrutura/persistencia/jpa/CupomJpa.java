package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.cupom.Cupom;
import recifecultural.dominio.cupom.CupomId;
import recifecultural.dominio.cupom.ICupomRepositorio;
import recifecultural.dominio.cupom.TipoDesconto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "cupom")
class CupomJpa {
    @Id
    String id;
    String codigo;
    @Enumerated(EnumType.STRING)
    TipoDesconto tipoDesconto;
    BigDecimal valorDesconto;
    BigDecimal valorMinimoPedido;
    int limiteGlobal;
    int usosGlobais;
    int limitePorCpf;
    LocalDateTime dataInicio;
    LocalDateTime dataFim;
    String categoriaPermitida;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cupom_cpf_usado", joinColumns = @JoinColumn(name = "cupom_id"))
    @Column(name = "cpf")
    Set<String> cpfsQueJaUsaram = new HashSet<>();
}

interface CupomJpaRepository extends JpaRepository<CupomJpa, String> {
    CupomJpa findByCodigo(String codigo);
}

@Repository
class CupomRepositorioImpl implements ICupomRepositorio {

    private final CupomJpaRepository jpa;
    private final JpaMapeador mapeador;

    CupomRepositorioImpl(CupomJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public Cupom buscarPorCodigo(String codigo) {
        var jpaObj = jpa.findByCodigo(codigo);
        return jpaObj != null ? mapeador.map(jpaObj, Cupom.class) : null;
    }

    @Override
    public Cupom buscarPorId(CupomId id) {
        return jpa.findById(id.getValor().toString())
                .map(c -> mapeador.map(c, Cupom.class))
                .orElse(null);
    }

    @Override
    public List<Cupom> listarTodos() {
        return jpa.findAll().stream()
                .map(c -> mapeador.map(c, Cupom.class))
                .toList();
    }

    @Override
    public void salvar(Cupom cupom) {
        jpa.save(mapeador.map(cupom, CupomJpa.class));
    }

    @Override
    public void deletar(CupomId id) {
        jpa.deleteById(id.getValor().toString());
    }
}
