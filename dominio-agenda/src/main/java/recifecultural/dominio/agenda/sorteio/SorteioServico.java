package recifecultural.dominio.agenda.sorteio;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.agenda.evento.StatusEvento;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SorteioServico {

    private final ISorteioRepositorio sorteioRepositorio;
    private final IEventoRepositorio eventoRepositorio;

    public SorteioServico(ISorteioRepositorio sorteioRepositorio, IEventoRepositorio eventoRepositorio) {
        this.sorteioRepositorio = sorteioRepositorio;
        this.eventoRepositorio = eventoRepositorio;
    }

    public Sorteio criar(UUID apresentacaoId, UUID eventoId, int vagas,
                         LocalDateTime prazoInscricao, LocalDateTime dataApresentacao) {
        Evento evento = eventoRepositorio.obter(eventoId)
                .orElseThrow(() -> new IllegalArgumentException("Evento não encontrado: " + eventoId));
        if (evento.getStatus() != StatusEvento.APROVADO)
            throw new IllegalStateException("Sorteio só pode ser criado para eventos aprovados.");

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
    }

    public void desistir(UUID sorteioId, UUID espectadorId) {
        Sorteio sorteio = buscarOuLancar(sorteioId);
        sorteio.desistir(espectadorId);
        sorteioRepositorio.atualizar(sorteio);
    }

    public void cancelar(UUID sorteioId) {
        Sorteio sorteio = buscarOuLancar(sorteioId);
        sorteio.cancelar();
        sorteioRepositorio.atualizar(sorteio);
    }

    public Optional<Sorteio> obter(UUID sorteioId) {
        return sorteioRepositorio.obter(sorteioId);
    }

    private Sorteio buscarOuLancar(UUID id) {
        return sorteioRepositorio.obter(id)
                .orElseThrow(() -> new IllegalArgumentException("Sorteio não encontrado: " + id));
    }
}
