package recifecultural.dominio.agenda.bloqueioadministrativo;

import recifecultural.dominio.agenda.Evento;
import recifecultural.dominio.agenda.IEventoRepositorio;

import java.security.InvalidParameterException;
import java.util.List;

public class BloqueioAdministrativoServico {
    private final IBloqueioAdministrativoRepositorio bloqueioRepositorio;
    private final IEventoRepositorio eventoRepositorio;

    public BloqueioAdministrativoServico(
            IBloqueioAdministrativoRepositorio bloqueioRepositorio,
            IEventoRepositorio eventoRepositorio) {

        if(bloqueioRepositorio == null) throw new InvalidParameterException("[BloqueioAdministrativoRepositorio] Repositório não pode ser nulo.");
        if(eventoRepositorio == null) throw new InvalidParameterException("[IEventoRepositorio] Repositório não pode ser nulo.");

        this.bloqueioRepositorio = bloqueioRepositorio;
        this.eventoRepositorio = eventoRepositorio;
    }

    public void criarBloqueio(BloqueioAdministrativo bloqueio) {
        if(bloqueio == null) throw new InvalidParameterException("Bloqueio Administrativo não pode ser nulo.");

        List<Evento> eventosConflitantes = eventoRepositorio.getEventosPorLocalEIntervalo(
                bloqueio.getIdLocal(),
                bloqueio.getDataInicio(),
                bloqueio.getDataFim()
        );

        String motivoCancelamento = "Cancelado devido a bloqueio administrativo: " + bloqueio.getMotivo();
        for (Evento evento : eventosConflitantes) {
            evento.cancelar(motivoCancelamento);
            eventoRepositorio.salvar(evento); // Persiste a mudança de estado
        }

        bloqueioRepositorio.salvar(bloqueio);
    }

    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return bloqueioRepositorio.obterTodos();
    }
}