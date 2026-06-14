package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;
import recifecultural.dominio.agenda.sorteio.StatusInscricao;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SorteioServico {

    private final ISorteioRepositorio sorteioRepositorio;
    private final IEventoRepositorio eventoRepositorio;
    private final INotificacaoServico notificacaoServico;

    public SorteioServico(ISorteioRepositorio sorteioRepositorio,
                           IEventoRepositorio eventoRepositorio,
                           INotificacaoServico notificacaoServico) {
        this.sorteioRepositorio = sorteioRepositorio;
        this.eventoRepositorio = eventoRepositorio;
        this.notificacaoServico = notificacaoServico;
    }

    public Sorteio criar(UUID apresentacaoId, UUID eventoId, int vagas,
                         LocalDateTime prazoInscricao, LocalDateTime dataApresentacao) {
        Evento evento = eventoRepositorio.obter(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + eventoId));
        evento.verificarAprovado();

        Sorteio sorteio = new Sorteio(apresentacaoId, eventoId, vagas, prazoInscricao, dataApresentacao);
        sorteioRepositorio.salvar(sorteio);
        return sorteio;
    }

    public void inscrever(UUID sorteioId, UUID espectadorId) {
        Sorteio sorteio = buscarOuLancar(sorteioId);
        sorteio.inscrever(espectadorId);
        sorteioRepositorio.atualizar(sorteio);
    }

    public void apurar(UUID sorteioId) {
        Sorteio sorteio = buscarOuLancar(sorteioId);
        sorteio.apurar();
        sorteioRepositorio.atualizar(sorteio);
        sorteio.getInscricoes().stream()
                .filter(i -> i.getStatus() == StatusInscricao.GANHADOR)
                .forEach(i -> notificacaoServico.enviarNotificacao(
                        i.getEspectadorId(),
                        "Parabéns! Você foi sorteado para o evento " + sorteio.getEventoId(),
                        "SORTEIO_GANHADOR", sorteioId));
    }

    public void desistir(UUID sorteioId, UUID espectadorId) {
        Sorteio sorteio = buscarOuLancar(sorteioId);
        sorteio.desistir(espectadorId).ifPresent(evento ->
                notificacaoServico.enviarNotificacao(
                        evento.getEspectadorPromovidoId(),
                        "Parabéns! Você foi promovido de suplente para ganhador no sorteio " + sorteioId,
                        "SORTEIO_PROMOCAO", sorteioId));
        sorteioRepositorio.atualizar(sorteio);
    }

    public void cancelar(UUID sorteioId) {
        Sorteio sorteio = buscarOuLancar(sorteioId);
        sorteio.cancelar();
        sorteioRepositorio.atualizar(sorteio);
        notificacaoServico.enviarBroadcast(
                "O sorteio " + sorteioId + " foi cancelado.",
                "SORTEIO_CANCELADO", sorteioId);
    }

    public Optional<Sorteio> obter(UUID sorteioId) {
        return sorteioRepositorio.obter(sorteioId);
    }

    public Iterable<Inscricao> iterarInscricoesPorPrioridade(UUID sorteioId) {
        if (!sorteioRepositorio.existe(sorteioId)) {
            throw new IllegalArgumentException("Sorteio não encontrado: " + sorteioId);
        }
        return new InscricoesOrdenadas(sorteioRepositorio, sorteioId);
    }

    private Sorteio buscarOuLancar(UUID id) {
        return sorteioRepositorio.obter(id)
                .orElseThrow(() -> new IllegalArgumentException("Sorteio não encontrado: " + id));
    }
}
