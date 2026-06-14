package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

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
        new InscreverOperacao(sorteioRepositorio, notificacaoServico, sorteioId, espectadorId).executar();
    }

    public void apurar(UUID sorteioId) {
        new ApurarOperacao(sorteioRepositorio, notificacaoServico, sorteioId).executar();
    }

    public void desistir(UUID sorteioId, UUID espectadorId) {
        new DesistirOperacao(sorteioRepositorio, notificacaoServico, sorteioId, espectadorId).executar();
    }

    public void cancelar(UUID sorteioId) {
        new CancelarOperacao(sorteioRepositorio, notificacaoServico, sorteioId).executar();
    }

    public Optional<Sorteio> obter(UUID sorteioId) {
        return sorteioRepositorio.obter(sorteioId);
    }
}
