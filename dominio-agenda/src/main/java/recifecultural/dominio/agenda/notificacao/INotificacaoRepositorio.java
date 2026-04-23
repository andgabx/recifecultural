package recifecultural.dominio.agenda.notificacao;

import java.util.List;
import java.util.UUID;

public interface INotificacaoRepositorio {
    boolean salvar(Notificacao notificacao);
    boolean atualizar(Notificacao notificacao);
    Notificacao obter(NotificacaoId id);
    List<Notificacao> obterPorUsuario(UUID usuarioAlvo);
    List<Notificacao> obterNaoLidasPorUsuario(UUID usuarioAlvo);
}