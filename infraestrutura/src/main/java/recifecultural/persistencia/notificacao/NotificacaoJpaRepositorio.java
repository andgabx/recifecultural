package recifecultural.persistencia.notificacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificacaoJpaRepositorio extends JpaRepository<NotificacaoJpa, UUID> {

    @Query("SELECT n FROM NotificacaoJpa n WHERE n.usuarioAlvo = :usuarioAlvo OR n.broadcast = true")
    List<NotificacaoJpa> findRelevantesParaUsuario(@Param("usuarioAlvo") UUID usuarioAlvo);
}