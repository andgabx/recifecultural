package recifecultural.agenda.bloqueioadministrativo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;

import java.util.List;

@RestController
@RequestMapping("/agenda/bloqueioadministrativo")
public class BloqueioAdministrativoControlador {

    private final BloqueioAdministrativoServicoAplicacao servicoAplicacao;

    @Autowired
    public BloqueioAdministrativoControlador(BloqueioAdministrativoServicoAplicacao servicoAplicacao) {
        this.servicoAplicacao = servicoAplicacao;
    }

    @PostMapping("/bloqueios")
    @ResponseStatus(HttpStatus.CREATED)
    public void criarBloqueio(@RequestBody BloqueioAdministrativo bloqueio) {
        servicoAplicacao.criarBloqueio(bloqueio);
    }

    @GetMapping("/bloqueios")
    public List<BloqueioAdministrativo> obterTodosBloqueios() {
        return servicoAplicacao.obterTodosBloqueios();
    }
}