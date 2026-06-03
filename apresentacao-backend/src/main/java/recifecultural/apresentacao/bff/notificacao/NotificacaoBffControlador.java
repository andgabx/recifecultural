package recifecultural.apresentacao.bff.notificacao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoId;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Notificações")
@RestController
@RequestMapping("/api/bff/notificacoes")
public class NotificacaoBffControlador extends AbstractBffControlador {

    private final INotificacaoServico servico;

    public NotificacaoBffControlador(INotificacaoServico servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista notificações de um usuário (todas ou só não-lidas)")
    @GetMapping
    public ResponseEntity<List<NotificacaoResumo>> listar(
            @RequestParam UUID usuarioId,
            @RequestParam(defaultValue = "false") boolean somenteNaoLidas) {
        List<Notificacao> notificacoes = somenteNaoLidas
                ? servico.obterNotificacoesNaoLidas(usuarioId)
                : servico.obterNotificacoesDoUsuario(usuarioId);
        List<NotificacaoResumo> resumos = notificacoes.stream()
                .map(NotificacaoResumo::de)
                .toList();
        return responder(resumos);
    }

    @Operation(summary = "Marca notificação como lida")
    @PostMapping("/{id}/marcar-lida")
    public ResponseEntity<Map<String, String>> marcarLida(@PathVariable UUID id) {
        servico.marcarComoLida(new NotificacaoId(id));
        return responderSemConteudo();
    }

    @Operation(summary = "Marca notificação como não-lida")
    @PostMapping("/{id}/marcar-nao-lida")
    public ResponseEntity<Map<String, String>> marcarNaoLida(@PathVariable UUID id) {
        servico.marcarComoNaoLida(new NotificacaoId(id));
        return responderSemConteudo();
    }

    @Operation(summary = "Envia broadcast — uso exclusivo do gestor")
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> broadcast(@RequestBody BroadcastRequisicao req) {
        servico.enviarBroadcast(req.mensagem(), req.contexto(), req.idReferencia());
        return responderSemConteudo();
    }

    public record BroadcastRequisicao(String mensagem, String contexto, UUID idReferencia) {}

    public record NotificacaoResumo(
            UUID id,
            UUID usuarioAlvo,
            String mensagem,
            String contexto,
            UUID idReferencia,
            boolean foiLida,
            LocalDateTime dataCriacao
    ) {
        static NotificacaoResumo de(Notificacao n) {
            return new NotificacaoResumo(
                    n.getId().valor(),
                    n.getUsuarioAlvo(),
                    n.getMensagem(),
                    n.getContexto(),
                    n.getIdReferencia(),
                    n.isFoiLida(),
                    n.getDataCriacao()
            );
        }
    }
}
