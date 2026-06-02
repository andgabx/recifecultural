package recifecultural.aplicacao.agenda.bloqueioadministrativo;

import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.time.LocalDate;
import java.util.List;

import static org.apache.commons.lang3.Validate.notNull;

public class BloqueioAdministrativoServicoAplicacao {

    private final BloqueioAdministrativoServico servico;
    private final BloqueioAdministrativoRepositorioAplicacao repositorio;

    public BloqueioAdministrativoServicoAplicacao(BloqueioAdministrativoServico servico,
                                                   BloqueioAdministrativoRepositorioAplicacao repositorio) {
        notNull(servico, "BloqueioAdministrativoServico não pode ser nulo.");
        notNull(repositorio, "BloqueioAdministrativoRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<BloqueioAdministrativoResumo> pesquisarAtivos() {
        return repositorio.pesquisarAtivos();
    }

    public void criar(EspacoId espacoId, LocalDate inicio, LocalDate fim, String justificativa) {
        servico.criarBloqueio(espacoId, inicio, fim, justificativa);
    }

    public void desativar(BloqueioAdministrativoId id) {
        servico.desativarBloqueio(id);
    }

    public List<EventoConflitanteResumo> previewConflitos(EspacoId espacoId, LocalDate inicio, LocalDate fim) {
        return servico.previewConflitantes(espacoId, inicio, fim).stream()
                .map(e -> new EventoConflitanteResumo(
                        e.getId().toString(),
                        e.getTitulo(),
                        e.getPeriodo() != null ? e.getPeriodo().getInicio().toString() : null,
                        e.getPeriodo() != null ? e.getPeriodo().getFim().toString() : null))
                .toList();
    }
}
