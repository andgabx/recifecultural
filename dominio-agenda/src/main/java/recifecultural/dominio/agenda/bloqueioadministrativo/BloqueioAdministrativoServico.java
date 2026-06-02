package recifecultural.dominio.agenda.bloqueioadministrativo;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoCanceladoPorBloqueioEvento;
import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class BloqueioAdministrativoServico {

    private final IBloqueioAdministrativoRepositorio bloqueioRepositorio;
    private final IEventoRepositorio eventoRepositorio;
    private final IEspacoRepositorio espacoRepositorio;
    private final EventoBarramento barramento;

    public BloqueioAdministrativoServico(
            IBloqueioAdministrativoRepositorio bloqueioRepositorio,
            IEventoRepositorio eventoRepositorio,
            IEspacoRepositorio espacoRepositorio,
            EventoBarramento barramento) {

        if (bloqueioRepositorio == null) throw new IllegalArgumentException("[IBloqueioAdministrativoRepositorio] Repositório não pode ser nulo.");
        if (eventoRepositorio == null) throw new IllegalArgumentException("[EventoRepositorio] Repositório não pode ser nulo.");
        if (espacoRepositorio == null) throw new IllegalArgumentException("[EspacoRepositorio] Repositório não pode ser nulo.");
        if (barramento == null) throw new IllegalArgumentException("[EventoBarramento] Barramento não pode ser nulo.");

        this.bloqueioRepositorio = bloqueioRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.espacoRepositorio = espacoRepositorio;
        this.barramento = barramento;
    }

    public BloqueioAdministrativo criarBloqueio(EspacoId espacoId, LocalDate inicio, LocalDate fim, String justificativa) {
        List<BloqueioAdministrativo> bloqueiosExistentes = bloqueioRepositorio.buscarPorEspaco(espacoId);

        boolean existeSobreposicao = bloqueiosExistentes.stream().anyMatch(bloqueio ->
                bloqueio.isAtivo() &&
                        (inicio.isBefore(bloqueio.getDataFim()) || inicio.isEqual(bloqueio.getDataFim())) &&
                        (fim.isAfter(bloqueio.getDataInicio()) || fim.isEqual(bloqueio.getDataInicio()))
        );

        if (existeSobreposicao) {
            throw new IllegalStateException("O sistema não permite a criação de um novo bloqueio para um espaço se já houver outro bloqueio naquele período.");
        }

        BloqueioAdministrativo novoBloqueio = new BloqueioAdministrativo(espacoId, inicio, fim, justificativa);

        Optional<Espaco> espacoOpt = espacoRepositorio.obterPorId(espacoId);
        if (espacoOpt.isPresent()) {
            Espaco espaco = espacoOpt.get();
            espaco.interditar();
            espacoRepositorio.atualizar(espaco);
        }

        cancelarEventosConflitantes(novoBloqueio);
        bloqueioRepositorio.salvar(novoBloqueio);

        return novoBloqueio;
    }

    public void desativarBloqueio(BloqueioAdministrativoId id) {
        BloqueioAdministrativo bloqueio = obterPorId(id);
        bloqueio.desativar();
        bloqueioRepositorio.atualizar(bloqueio);

        Optional<Espaco> espacoOpt = espacoRepositorio.obterPorId(bloqueio.getEspacoId());
        if (espacoOpt.isPresent()) {
            Espaco espaco = espacoOpt.get();
            espaco.reativar();
            espacoRepositorio.atualizar(espaco);
        }
    }

    public void desativarBloqueiosAtivosDoEspaco(EspacoId espacoId) {
        bloqueioRepositorio.buscarPorEspaco(espacoId).stream()
                .filter(BloqueioAdministrativo::isAtivo)
                .forEach(b -> {
                    b.desativar();
                    bloqueioRepositorio.atualizar(b);
                });
    }

    public BloqueioAdministrativo obterPorId(BloqueioAdministrativoId id) {
        if (id == null) throw new IllegalArgumentException("ID do bloqueio é obrigatório.");

        BloqueioAdministrativo bloqueio = bloqueioRepositorio.obter(id);
        if (bloqueio == null) throw new IllegalArgumentException("Bloqueio Administrativo não encontrado.");

        return bloqueio;
    }

    public void atualizarBloqueio(BloqueioAdministrativoId id, String novaJustificativa, LocalDate novoInicio, LocalDate novoFim) {
        BloqueioAdministrativo bloqueio = obterPorId(id);
        bloqueio.atualizarInformacoes(novaJustificativa, novoInicio, novoFim);
        cancelarEventosConflitantes(bloqueio);
        bloqueioRepositorio.atualizar(bloqueio);
    }

    public void deletarBloqueio(BloqueioAdministrativoId id) {
        obterPorId(id);
        bloqueioRepositorio.deletar(id);
    }

    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return bloqueioRepositorio.obterTodos();
    }

    private void cancelarEventosConflitantes(BloqueioAdministrativo bloqueio) {
        List<Evento> eventosConflitantes = eventoRepositorio.obterPorLocalEIntervalo(
                bloqueio.getEspacoId().valor(),
                bloqueio.getDataInicio().atTime(LocalTime.MAX),
                bloqueio.getDataFim().atTime(LocalTime.MAX)
        );

        if (eventosConflitantes == null || eventosConflitantes.isEmpty()) {
            return;
        }

        String motivoCancelamento = "Cancelado devido a bloqueio administrativo: " + bloqueio.getJustificativa();

        for (Evento evento : eventosConflitantes) {
            evento.cancelar(motivoCancelamento);
            eventoRepositorio.atualizar(evento);

            // Padrão Observer (Par 3): publica evento de domínio no barramento.
            // O contexto de Notificações assina via observador e dispara as mensagens.
            barramento.postar(new EventoCanceladoPorBloqueioEvento(
                    evento.getId(),
                    evento.getPromotorId(),
                    evento.getTitulo(),
                    bloqueio.getJustificativa()
            ));
        }
    }
}