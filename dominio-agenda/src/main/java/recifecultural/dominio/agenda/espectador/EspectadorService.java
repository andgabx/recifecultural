package recifecultural.dominio.agenda.espectador;

import java.util.Optional;

public class EspectadorService {

    private final EspectadorRepositorio repositorio;

    public EspectadorService(EspectadorRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void salvar(Espectador espectador) {
        repositorio.salvar(espectador);
    }

    public Optional<Espectador> obter(EspectadorId id) {
        return repositorio.obter(id);
    }

    public void deletar(EspectadorId id) {
        repositorio.deletar(id);
    }
}
