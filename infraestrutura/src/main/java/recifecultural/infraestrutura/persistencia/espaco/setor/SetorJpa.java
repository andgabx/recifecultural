package recifecultural.infraestrutura.persistencia.espaco.setor;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.setor.StatusAssento;
import recifecultural.dominio.espaco.setor.TipoSetor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "setor")
public class SetorJpa {
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
