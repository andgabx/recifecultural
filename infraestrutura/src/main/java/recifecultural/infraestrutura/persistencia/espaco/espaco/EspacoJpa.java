package recifecultural.infraestrutura.persistencia.espaco.espaco;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import recifecultural.dominio.espaco.espaco.StatusEspaco;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "espaco")
public class EspacoJpa {
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
