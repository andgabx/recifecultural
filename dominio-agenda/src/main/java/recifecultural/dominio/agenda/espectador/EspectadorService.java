package recifecultural.dominio.agenda.espectador;

import java.util.Optional;
import java.util.UUID;

public class EspectadorService {

    private final EspectadorRepositorio repositorio;

    public EspectadorService(EspectadorRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void salvar(Espectador espectador) {
        repositorio.salvar(espectador);
    }

    public Optional<Espectador> obter(UUID id) {
        return repositorio.obter(id);
    }

    public void deletar(UUID id) {
        repositorio.deletar(id);
    }
}
