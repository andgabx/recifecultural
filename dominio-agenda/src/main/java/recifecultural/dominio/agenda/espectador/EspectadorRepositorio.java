package recifecultural.dominio.agenda.espectador;

import java.util.Optional;
import java.util.UUID;

public interface EspectadorRepositorio {
    void salvar(Espectador espectador);
    Optional<Espectador> obter(UUID id);
    void atualizar(Espectador espectador);
    void deletar(UUID id);
}
