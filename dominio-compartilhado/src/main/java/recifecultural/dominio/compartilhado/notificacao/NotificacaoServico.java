package recifecultural.dominio.compartilhado.notificacao;

import java.util.List;
import java.util.UUID;

public class NotificacaoServico implements INotificacaoServico {
    private final INotificacaoRepositorio notificacaoRepositorio;
    private final IUsuarioContextoServico usuarioContextoServico;

    public NotificacaoServico(INotificacaoRepositorio notificacaoRepositorio, IUsuarioContextoServico usuarioContextoServico) {
        if (notificacaoRepositorio == null) {
            throw new IllegalArgumentException("[INotificacaoRepositorio] Repositório não pode ser nulo.");
        }
        if (usuarioContextoServico == null) {
            throw new IllegalArgumentException("[IUsuarioContextoServico] Serviço não pode ser nulo.");
        }
        this.notificacaoRepositorio = notificacaoRepositorio;
        this.usuarioContextoServico = usuarioContextoServico;
    }

    public void enviarNotificacao(UUID usuarioAlvo, String mensagem) {
        enviarNotificacao(usuarioAlvo, mensagem, null, null);
    }

    public void enviarNotificacao(UUID usuarioAlvo, String mensagem, String contexto, UUID idReferencia) {
        if (usuarioAlvo == null) throw new IllegalArgumentException("O usuário alvo é obrigatório para enviar uma notificação.");
        if (mensagem == null || mensagem.isBlank()) throw new IllegalArgumentException("A mensagem é obrigatória para enviar uma notificação.");

        Notificacao notificacao = new Notificacao(usuarioAlvo, mensagem, contexto, idReferencia);
        notificacaoRepositorio.salvar(notificacao);
    }

    public void enviarBroadcast(String mensagem) {
        enviarBroadcast(mensagem, "TODOS_USUARIOS", null);
    }

    public void enviarBroadcast(String mensagem, String contexto, UUID idReferencia) {
        if (mensagem == null || mensagem.isBlank()) throw new IllegalArgumentException("A mensagem é obrigatória para enviar um broadcast.");

        List<UUID> destinatarios = usuarioContextoServico.obterUsuariosPorContexto(contexto, idReferencia);

        if (destinatarios != null) {
            for (UUID usuarioId : destinatarios) {
                Notificacao notificacao = new Notificacao(usuarioId, mensagem, contexto, idReferencia);
                notificacaoRepositorio.salvar(notificacao);
            }
        }
    }

    public void marcarComoLida(NotificacaoId id) {
        Notificacao notificacao = buscarNotificacao(id);
        notificacao.marcarComoLida();
        notificacaoRepositorio.atualizar(notificacao);
    }

    public void marcarComoNaoLida(NotificacaoId id) {
        Notificacao notificacao = buscarNotificacao(id);
        notificacao.marcarComoNaoLida();
        notificacaoRepositorio.atualizar(notificacao);
    }

    public List<Notificacao> obterNotificacoesDoUsuario(UUID usuarioAlvo) {
        if (usuarioAlvo == null) throw new IllegalArgumentException("O usuário alvo é obrigatório.");
        return notificacaoRepositorio.obterPorUsuario(usuarioAlvo);
    }

    public List<Notificacao> obterNotificacoesNaoLidas(UUID usuarioAlvo) {
        if (usuarioAlvo == null) throw new IllegalArgumentException("O usuário alvo é obrigatório.");
        return notificacaoRepositorio.obterNaoLidasPorUsuario(usuarioAlvo);
    }

    private Notificacao buscarNotificacao(NotificacaoId id) {
        if (id == null) throw new IllegalArgumentException("O ID da notificação não pode ser nulo.");
        Notificacao notificacao = notificacaoRepositorio.obter(id);
        if (notificacao == null) throw new IllegalArgumentException("Notificação não encontrada.");
        return notificacao;
    }
}