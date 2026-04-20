package recifecultural.agenda.bloqueioadministrativo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoServico;

import java.util.List;

@Service
public class BloqueioAdministrativoServicoAplicacao {

    private final BloqueioAdministrativoServico bloqueioAdministrativoServico;

    public BloqueioAdministrativoServicoAplicacao(BloqueioAdministrativoServico bloqueioAdministrativoServico) {
        this.bloqueioAdministrativoServico = bloqueioAdministrativoServico;
    }

    @Transactional
    public void criarBloqueio(BloqueioAdministrativo bloqueio) {
        bloqueioAdministrativoServico.criarBloqueio(bloqueio);
    }

    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return bloqueioAdministrativoServico.obterTodosBloqueios();
    }
}