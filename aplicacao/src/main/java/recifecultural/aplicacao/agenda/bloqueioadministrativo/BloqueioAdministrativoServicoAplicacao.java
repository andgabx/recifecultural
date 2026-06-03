package recifecultural.aplicacao.agenda.bloqueioadministrativo;

import recifecultural.aplicacao.ingressos.IngressoRepositorioAplicacao;
import recifecultural.aplicacao.ingressos.IngressoResumo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class BloqueioAdministrativoServicoAplicacao {

    private final BloqueioAdministrativoServico servico;
    private final BloqueioAdministrativoRepositorioAplicacao repositorio;
    private final IngressoRepositorioAplicacao ingressoRepositorio;

    public BloqueioAdministrativoServicoAplicacao(BloqueioAdministrativoServico servico,
                                                   BloqueioAdministrativoRepositorioAplicacao repositorio,
                                                   IngressoRepositorioAplicacao ingressoRepositorio) {
        notNull(servico, "BloqueioAdministrativoServico não pode ser nulo.");
        notNull(repositorio, "BloqueioAdministrativoRepositorioAplicacao não pode ser nulo.");
        notNull(ingressoRepositorio, "IngressoRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
        this.ingressoRepositorio = ingressoRepositorio;
    }

    public List<BloqueioAdministrativoResumo> pesquisarAtivos() {
        return repositorio.pesquisarAtivos();
    }

    public List<BloqueioAdministrativoResumo> pesquisarTodos() {
        return repositorio.pesquisarTodos();
    }

    public void criar(EspacoId espacoId, LocalDate inicio, LocalDate fim, String justificativa) {
        servico.criarBloqueio(espacoId, inicio, fim, justificativa);
    }

    public void desativar(BloqueioAdministrativoId id, boolean reativarEventos) {
        servico.desativarBloqueio(id, reativarEventos);
    }

    public List<EventoConflitanteResumo> previewConflitos(EspacoId espacoId, LocalDate inicio, LocalDate fim) {
        return servico.previewConflitantes(espacoId, inicio, fim).stream()
                .map(e -> {
                    var ativos = ingressoRepositorio.pesquisarPorEvento(UUID.fromString(e.getId().toString()))
                            .stream()
                            .filter(i -> "ATIVO".equals(i.getStatus()))
                            .toList();
                    return new EventoConflitanteResumo(
                            e.getId().toString(),
                            e.getTitulo(),
                            e.getPeriodo() != null ? e.getPeriodo().getInicio().toString() : null,
                            e.getPeriodo() != null ? e.getPeriodo().getFim().toString() : null,
                            ativos.size(),
                            calcularTotalReembolso(ativos));
                })
                .toList();
    }

    private static BigDecimal calcularTotalReembolso(List<IngressoResumo> ingressos) {
        return ingressos.stream()
                .map(i -> new BigDecimal(i.getValorPago()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void desativarBloqueiosAtivosDoEspaco(EspacoId espacoId) {
        servico.desativarBloqueiosAtivosDoEspaco(espacoId);
    }
}
