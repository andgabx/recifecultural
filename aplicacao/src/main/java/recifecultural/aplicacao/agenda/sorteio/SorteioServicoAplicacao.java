package recifecultural.aplicacao.agenda.sorteio;

import recifecultural.dominio.agenda.sorteio.Inscricao;
import recifecultural.dominio.agenda.sorteio.Sorteio;
import recifecultural.dominio.agenda.sorteio.SorteioServico;
import recifecultural.dominio.agenda.sorteio.StatusInscricao;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class SorteioServicoAplicacao {

    private final SorteioServico servico;
    private final SorteioRepositorioAplicacao repositorio;

    public SorteioServicoAplicacao(SorteioServico servico, SorteioRepositorioAplicacao repositorio) {
        notNull(servico, "SorteioServico não pode ser nulo.");
        notNull(repositorio, "SorteioRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<SorteioResumo> pesquisarPorEvento(UUID eventoId) {
        return repositorio.pesquisarPorEvento(eventoId);
    }

    public List<SorteioInscritoResumo> pesquisarPorEspectador(UUID espectadorId) {
        return repositorio.pesquisarPorEspectador(espectadorId);
    }

    public List<SorteioResumo> pesquisarAbertos() {
        return repositorio.pesquisarAbertos();
    }

    public void criar(UUID apresentacaoId, UUID eventoId, int vagas,
                      LocalDateTime prazoInscricao, LocalDateTime dataApresentacao) {
        servico.criar(apresentacaoId, eventoId, vagas, prazoInscricao, dataApresentacao);
    }

    public void inscrever(UUID sorteioId, UUID espectadorId) {
        servico.inscrever(sorteioId, espectadorId);
    }

    public void apurar(UUID sorteioId) {
        servico.apurar(sorteioId);
    }

    public void desistir(UUID sorteioId, UUID espectadorId) {
        servico.desistir(sorteioId, espectadorId);
    }

    public void cancelar(UUID sorteioId) {
        servico.cancelar(sorteioId);
    }

    // Retorna inscrições do sorteio em ordem de prioridade (GANHADOR → SUPLENTE → INSCRITO → ...)
    public List<Inscricao> listarInscricoesPorPrioridade(UUID sorteioId) {
        Sorteio sorteio = servico.obter(sorteioId)
                .orElseThrow(() -> new IllegalArgumentException("Sorteio não encontrado."));
        return sorteio.getInscricoes().stream()
                .sorted(Comparator.comparingInt(i -> prioridade(i.getStatus())))
                .toList();
    }

    private static int prioridade(StatusInscricao status) {
        return switch (status) {
            case GANHADOR -> 0;
            case SUPLENTE -> 1;
            case INSCRITO -> 2;
            case DESISTENTE -> 3;
            case CANCELADA -> 4;
        };
    }
}
