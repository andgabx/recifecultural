package recifecultural.agenda.bloqueioadministrativo;

import org.springframework.transaction.annotation.Transactional;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;

import java.util.List;

public class BloqueioAdministrativoServicoAplicacao {

    private final BloqueioAdministrativoServico dominioServico;

    public BloqueioAdministrativoServicoAplicacao(BloqueioAdministrativoServico dominioServico) {
        this.dominioServico = dominioServico;
    }

    @Transactional
    public void criarBloqueio(BloqueioAdministrativo bloqueio) {
        dominioServico.criarBloqueio(bloqueio);
    }

    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return dominioServico.obterTodosBloqueios();
    }
}