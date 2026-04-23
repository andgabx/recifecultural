package recifecultural.agenda.notificacao;

import org.springframework.transaction.annotation.Transactional;
import recifecultural.dominio.agenda.notificacao.Notificacao;
import recifecultural.dominio.agenda.notificacao.NotificacaoId;
import recifecultural.dominio.agenda.notificacao.NotificacaoServico;

import java.util.List;
import java.util.UUID;

public class NotificacaoServicoAplicacao {

    private final NotificacaoServico dominioServico;

    public NotificacaoServicoAplicacao(NotificacaoServico dominioServico) {
        this.dominioServico = dominioServico;
    }

    @Transactional
    public void enviarNotificacao(UUID usuarioAlvo, String mensagem) {
        dominioServico.enviarNotificacao(usuarioAlvo, mensagem);
    }

    @Transactional
    public void enviarBroadcast(String mensagem) {
        dominioServico.enviarBroadcast(mensagem);
    }

    @Transactional
    public void marcarComoLida(UUID id, UUID usuarioId) {
        dominioServico.marcarComoLida(new NotificacaoId(id), usuarioId);
    }

    public List<Notificacao> obterNotificacoesDoUsuario(UUID usuarioAlvo) {
        return dominioServico.obterNotificacoesDoUsuario(usuarioAlvo);
    }

    public List<Notificacao> obterNotificacoesNaoLidas(UUID usuarioAlvo) {
        return dominioServico.obterNotificacoesNaoLidas(usuarioAlvo);
    }
}