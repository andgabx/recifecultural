package recifecultural.dominio.compartilhado.notificacao;

import java.util.List;
import java.util.UUID;

public interface INotificacaoServico {
    void enviarNotificacao(UUID usuarioAlvo, String mensagem);
    void enviarNotificacao(UUID usuarioAlvo, String mensagem, String contexto, UUID idReferencia);

    void enviarBroadcast(String mensagem);
    void enviarBroadcast(String mensagem, String contexto, UUID idReferencia);

    void marcarComoLida(NotificacaoId id);
    void marcarComoNaoLida(NotificacaoId id);

    List<Notificacao> obterNotificacoesDoUsuario(UUID usuarioAlvo);
    List<Notificacao> obterNotificacoesNaoLidas(UUID usuarioAlvo);
}
