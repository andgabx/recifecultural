package recifecultural.dominio.agenda.bloqueioadministrativo;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;

import java.time.LocalDateTime;
import java.util.List;

public class BloqueioAdministrativoServico {
    private final IBloqueioAdministrativoRepositorio bloqueioRepositorio;
    private final IEventoRepositorio eventoRepositorio;

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

        cancelarEventosConflitantes(bloqueio);
        bloqueioRepositorio.salvar(bloqueio);
    }

    public BloqueioAdministrativo obterPorId(BloqueioAdministrativoId id) {
        if(id == null) throw new IllegalArgumentException("ID do bloqueio é obrigatório.");
        BloqueioAdministrativo bloqueio = bloqueioRepositorio.obter(id);
        if (bloqueio == null) throw new IllegalArgumentException("Bloqueio Administrativo não encontrado.");
        return bloqueio;
    }

    public void atualizarBloqueio(BloqueioAdministrativoId id, String novoMotivo, LocalDateTime novoInicio, LocalDateTime novoFim) {
        BloqueioAdministrativo bloqueio = obterPorId(id);

        bloqueio.setMotivo(novoMotivo);
        bloqueio.setPeriodo(novoInicio, novoFim);

        cancelarEventosConflitantes(bloqueio);

        bloqueioRepositorio.atualizar(bloqueio);
    }

    public void deletarBloqueio(BloqueioAdministrativoId id) {
        BloqueioAdministrativo bloqueio = obterPorId(id);
        bloqueioRepositorio.deletar(bloqueio.getId());
    }

    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return bloqueioRepositorio.obterTodos();
    }

    private void cancelarEventosConflitantes(BloqueioAdministrativo bloqueio) {
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
    }
}