package recifecultural.aplicacao.agenda.sorteio;

import recifecultural.dominio.agenda.sorteio.Inscricao;
import recifecultural.dominio.agenda.sorteio.SorteioServico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

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

    // Iterator: retorna inscrições do sorteio em ordem de prioridade (GANHADOR → SUPLENTE → INSCRITO → ...)
    public List<Inscricao> listarInscricoesPorPrioridade(UUID sorteioId) {
        Iterable<Inscricao> ordenadas = servico.iterarInscricoesPorPrioridade(sorteioId);
        return StreamSupport.stream(ordenadas.spliterator(), false).toList();
    }
}
