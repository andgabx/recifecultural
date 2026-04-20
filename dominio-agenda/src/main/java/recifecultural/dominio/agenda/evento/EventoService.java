package recifecultural.dominio.agenda.evento;

import java.util.Optional;

public class EventoService {

    private final EventoRepositorio repositorio;

    public EventoService(EventoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void salvar(Evento evento) {
        repositorio.salvar(evento);
    }

    public Optional<Evento> obter(EventoId id) {
        return repositorio.obter(id);
    }

    public void submeterParaAnalise(EventoId id) {
        Evento evento = buscarOuLancar(id);
        evento.submeterParaAnalise();
        repositorio.atualizar(evento);
    }

    public void aprovar(EventoId id) {
        Evento evento = buscarOuLancar(id);
        evento.aprovar();
        repositorio.atualizar(evento);
    }

    public void reprovar(EventoId id, FeedbackReprovacao feedback) {
        Evento evento = buscarOuLancar(id);
        evento.reprovar(feedback);
        repositorio.atualizar(evento);
    }

    public void deletar(EventoId id) {
        repositorio.deletar(id);
    }

    private Evento buscarOuLancar(EventoId id) {
        return repositorio.obter(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + id));
    }
}
