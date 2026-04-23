package recifecultural.agenda.notificacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import recifecultural.dominio.agenda.notificacao.Notificacao;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/agenda/notificacoes")
public class NotificacaoControlador {

    private final NotificacaoServicoAplicacao servicoAplicacao;

    @Autowired
    public NotificacaoControlador(NotificacaoServicoAplicacao servicoAplicacao) {
        this.servicoAplicacao = servicoAplicacao;
    }

    public static class CriarNotificacaoRequest {
        public UUID usuarioAlvo;
        public String mensagem;
    }

    public static class CriarBroadcastRequest {
        public String mensagem;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void enviarNotificacao(@RequestBody CriarNotificacaoRequest request) {
        servicoAplicacao.enviarNotificacao(request.usuarioAlvo, request.mensagem);
    }

    @PostMapping("/broadcast")
    @ResponseStatus(HttpStatus.CREATED)
    public void enviarBroadcast(@RequestBody CriarBroadcastRequest request) {
        servicoAplicacao.enviarBroadcast(request.mensagem);
    }

    @PatchMapping("/{id}/lida/usuario/{usuarioId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void marcarComoLida(@PathVariable UUID id, @PathVariable UUID usuarioId) {
        servicoAplicacao.marcarComoLida(id, usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Notificacao> obterNotificacoesDoUsuario(@PathVariable UUID usuarioId) {
        return servicoAplicacao.obterNotificacoesDoUsuario(usuarioId);
    }

    @GetMapping("/usuario/{usuarioId}/nao-lidas")
    public List<Notificacao> obterNotificacoesNaoLidas(@PathVariable UUID usuarioId) {
        return servicoAplicacao.obterNotificacoesNaoLidas(usuarioId);
    }
}