package recifecultural.persistencia;

import org.springframework.stereotype.Component;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class EventoRepositorioStubImpl implements IEventoRepositorio {

    @Override
    public void salvar(Evento evento) {}

    @Override
    public void atualizar(Evento evento) {}

    @Override
    public void deletar(UUID id) {}

    @Override
    public Optional<Evento> obter(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Evento> obterTodos() {
        return Collections.emptyList();
    }

    @Override
    public List<Evento> obterPorLocalEIntervalo(UUID localId, LocalDateTime inicio, LocalDateTime fim) {
        return Collections.emptyList();
    }
}