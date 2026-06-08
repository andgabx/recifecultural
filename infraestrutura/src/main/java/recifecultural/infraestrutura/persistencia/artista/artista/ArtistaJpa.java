package recifecultural.infraestrutura.persistencia.artista.artista;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.artista.artista.ItemRider;
import recifecultural.dominio.artista.artista.StatusArtista;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "artista")
public class ArtistaJpa {
    @Id
    UUID id;
    UUID produtorId;
    String nome;
    @Enumerated(EnumType.STRING)
    StatusArtista status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "artista_rider_item", joinColumns = @JoinColumn(name = "artista_id"))
    @Column(name = "item")
    @Enumerated(EnumType.STRING)
    Set<ItemRider> riderItens = new HashSet<>();
}

interface ArtistaJpaRepository extends JpaRepository<ArtistaJpa, UUID> {
    List<ArtistaJpa> findByProdutorId(UUID produtorId);

    @Query("SELECT COUNT(a) > 0 FROM ArtistaJpa a WHERE a.nome = :nome AND a.produtorId = :produtorId")
    boolean existsByNomeAndProdutorId(String nome, UUID produtorId);
}
