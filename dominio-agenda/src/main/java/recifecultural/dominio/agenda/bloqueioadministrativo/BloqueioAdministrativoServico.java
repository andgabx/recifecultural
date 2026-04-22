package recifecultural.dominio.agenda.bloqueioadministrativo;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;

import java.util.List;

public class BloqueioAdministrativoServico {
    private final IBloqueioAdministrativoRepositorio bloqueioRepositorio;
    private final IEventoRepositorio eventoRepositorio; // Utilizando repositório unificado

    public BloqueioAdministrativoServico(
            IBloqueioAdministrativoRepositorio bloqueioRepositorio,
            IEventoRepositorio eventoRepositorio) {

        if(bloqueioRepositorio == null) throw new IllegalArgumentException("[IBloqueioAdministrativoRepositorio] Repositório não pode ser nulo.");
        if(eventoRepositorio == null) throw new IllegalArgumentException("[EventoRepositorio] Repositório não pode ser nulo.");

        this.bloqueioRepositorio = bloqueioRepositorio;
        this.eventoRepositorio = eventoRepositorio;
    }

    public void criarBloqueio(BloqueioAdministrativo bloqueio) {
        if(bloqueio == null) throw new IllegalArgumentException("Bloqueio Administrativo não pode ser nulo.");

        List<Evento> eventosConflitantes = eventoRepositorio.obterPorLocalEIntervalo(
                bloqueio.getIdEspaco(),
                bloqueio.getDataInicio(),
                bloqueio.getDataFim()
        );

        String motivoCancelamento = "Cancelado devido a bloqueio administrativo: " + bloqueio.getMotivo();
        for (Evento evento : eventosConflitantes) {
            evento.cancelar(motivoCancelamento);
            eventoRepositorio.atualizar(evento);
        }

        bloqueioRepositorio.salvar(bloqueio);
    }

    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return bloqueioRepositorio.obterTodos();
    }
}