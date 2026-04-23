package recifecultural.agenda.bloqueioadministrativo;

import org.springframework.transaction.annotation.Transactional;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;

import java.util.List;
import java.util.UUID;

public class BloqueioAdministrativoServicoAplicacao {

    private final BloqueioAdministrativoServico dominioServico;

    public BloqueioAdministrativoServicoAplicacao(BloqueioAdministrativoServico dominioServico) {
        this.dominioServico = dominioServico;
    }

    @Transactional
    public void criarBloqueio(BloqueioAdministrativo bloqueio) {
        dominioServico.criarBloqueio(bloqueio);
    }

    @Transactional(readOnly = true)
    public BloqueioAdministrativo obterPorId(UUID id) {
        return dominioServico.obterPorId(new BloqueioAdministrativoId(id));
    }

    @Transactional
    public void atualizarBloqueio(UUID id, BloqueioAdministrativo bloqueioAtualizado) {
        dominioServico.atualizarBloqueio(
                new BloqueioAdministrativoId(id),
                bloqueioAtualizado.getMotivo(),
                bloqueioAtualizado.getDataInicio(),
                bloqueioAtualizado.getDataFim()
        );
    }

    @Transactional
    public void deletarBloqueio(UUID id) {
        dominioServico.deletarBloqueio(new BloqueioAdministrativoId(id));
    }

    @Transactional(readOnly = true)
    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return dominioServico.obterTodosBloqueios();
    }
}