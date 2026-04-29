package recifecultural.dominio.compartilhado.notificacao;

import java.util.List;
import java.util.UUID;

public class NotificacaoServico {
    private final INotificacaoRepositorio notificacaoRepositorio;

    public NotificacaoServico(INotificacaoRepositorio notificacaoRepositorio) {
        if (notificacaoRepositorio == null) {
            throw new IllegalArgumentException("[INotificacaoRepositorio] Repositório não pode ser nulo.");
        }
        this.notificacaoRepositorio = notificacaoRepositorio;
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
        enviarBroadcast(mensagem, null, null);
    }

    public void enviarBroadcast(String mensagem, String contexto, UUID idReferencia) {
        if (mensagem == null || mensagem.isBlank()) throw new IllegalArgumentException("A mensagem é obrigatória para enviar um broadcast.");

        Notificacao notificacao = Notificacao.criarBroadcast(mensagem, contexto, idReferencia);
        notificacaoRepositorio.salvar(notificacao);
    }

    public void marcarComoLida(NotificacaoId id, UUID usuarioId) {
        Notificacao notificacao = buscarNotificacao(id);
        notificacao.marcarComoLida(usuarioId);
        notificacaoRepositorio.atualizar(notificacao);
    }

    public void marcarComoNaoLida(NotificacaoId id, UUID usuarioId) {
        Notificacao notificacao = buscarNotificacao(id);
        notificacao.marcarComoNaoLida(usuarioId);
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