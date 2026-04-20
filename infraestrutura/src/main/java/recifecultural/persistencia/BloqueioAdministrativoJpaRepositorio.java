package recifecultural.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BloqueioAdministrativoJpaRepositorio extends JpaRepository<BloqueioAdministrativoJpa, UUID> { }