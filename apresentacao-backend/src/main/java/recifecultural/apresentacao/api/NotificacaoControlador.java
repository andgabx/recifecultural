package recifecultural.apresentacao.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;

import java.util.Map;
import java.util.UUID;

@Tag(name = "API — Notificações (Gestor)")
@RestController
@RequestMapping("/api/notificacoes")
public class NotificacaoControlador extends AbstractBffControlador {

    private final INotificacaoServico servico;

    public NotificacaoControlador(INotificacaoServico servico) {
        this.servico = servico;
    }

    @Operation(summary = "Envia broadcast para um grupo de usuários",
               description = """
                       Contextos disponíveis:
                       - TITULARES_INGRESSOS_EVENTO — compradores de ingresso de um evento (idReferencia = eventoId)
                       - ARTISTAS_EVENTO            — artistas escalados em um evento (idReferencia = eventoId)
                       - TODOS_ARTISTAS             — todos os artistas cadastrados
                       - TODOS_USUARIOS             — todos os usuários do sistema
                       - PROMOTORES                 — todos os promotores/produtores
                       """)
    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, String>> enviarBroadcast(@RequestBody BroadcastRequisicao req) {
        if (req.mensagem() == null || req.mensagem().isBlank())
            throw new IllegalArgumentException("Mensagem não pode ser vazia.");
        if (req.contexto() == null)
            throw new IllegalArgumentException("Contexto é obrigatório.");

        servico.enviarBroadcast(req.mensagem(), req.contexto().name(), req.idReferencia());
        return responderSemConteudo();
    }

    public enum ContextoBroadcast {
        TITULARES_INGRESSOS_EVENTO,
        ARTISTAS_EVENTO,
        TODOS_ARTISTAS,
        TODOS_USUARIOS,
        PROMOTORES
    }

    public record BroadcastRequisicao(
            String mensagem,
            ContextoBroadcast contexto,
            UUID idReferencia
    ) {}
}
