package recifecultural.infraestrutura.persistencia.compartilhado.notificacao;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "notificacao")
public class NotificacaoJpa {
    @Id
    UUID id;
    UUID usuarioAlvo;
    @Column(length = 2000)
    String mensagem;
    String contexto;
    UUID idReferencia;
    boolean foiLida;
    LocalDateTime dataCriacao;
}

interface NotificacaoJpaRepository extends JpaRepository<NotificacaoJpa, UUID> {
    List<NotificacaoJpa> findByUsuarioAlvoOrderByDataCriacaoDesc(UUID usuarioAlvo);
    List<NotificacaoJpa> findByUsuarioAlvoAndFoiLidaFalseOrderByDataCriacaoDesc(UUID usuarioAlvo);
    List<NotificacaoJpa> findByUsuarioAlvoAndContextoOrderByDataCriacaoDesc(UUID usuarioAlvo, String contexto);
}
