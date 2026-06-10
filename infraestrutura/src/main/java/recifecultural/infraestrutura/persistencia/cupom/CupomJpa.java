package recifecultural.infraestrutura.persistencia.cupom;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.cupom.TipoDesconto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cupom")
public class CupomJpa {
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
