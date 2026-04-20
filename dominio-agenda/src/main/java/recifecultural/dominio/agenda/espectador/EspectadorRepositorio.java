package recifecultural.dominio.agenda.espectador;

import java.util.Optional;

public interface EspectadorRepositorio {
    void salvar(Espectador espectador);
    Optional<Espectador> obter(EspectadorId id);
    void atualizar(Espectador espectador);
    void deletar(EspectadorId id);
}
