package recifecultural.dominio.agenda.evento;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.EventoRepositorio;
import recifecultural.dominio.agenda.evento.FeedbackReprovacao;

import java.util.Optional;
import java.util.UUID;

public class EventoService {

    private final EventoRepositorio repositorio;

    public EventoService(EventoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void salvar(Evento evento) {
        repositorio.salvar(evento);
    }

    public Optional<Evento> obter(UUID id) {
        return repositorio.obter(id);
    }

    public void submeterParaAnalise(UUID id) {
        Evento evento = buscarOuLancar(id);
        evento.submeterParaAnalise();
        repositorio.atualizar(evento);
    }

    public void aprovar(UUID id) {
        Evento evento = buscarOuLancar(id);
        evento.aprovar();
        repositorio.atualizar(evento);
    }

    public void reprovar(UUID id, FeedbackReprovacao feedback) {
        Evento evento = buscarOuLancar(id);
        evento.reprovar(feedback);
        repositorio.atualizar(evento);
    }

    public void deletar(UUID id) {
        repositorio.deletar(id);
    }

    private Evento buscarOuLancar(UUID id) {
        return repositorio.obter(id)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + id));
    }
}
