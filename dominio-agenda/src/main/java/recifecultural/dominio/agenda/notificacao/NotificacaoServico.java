package recifecultural.dominio.agenda.notificacao;

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

    // ... existing code ...
    public void enviarNotificacao(UUID usuarioAlvo, String mensagem) {
        if (usuarioAlvo == null) {
            throw new IllegalArgumentException("O usuário alvo é obrigatório para enviar uma notificação.");
        }
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem é obrigatória para enviar uma notificação.");
        }

        Notificacao notificacao = new Notificacao(usuarioAlvo, mensagem);
        notificacaoRepositorio.salvar(notificacao);
    }

    public void enviarBroadcast(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem é obrigatória para enviar um broadcast.");
        }
        Notificacao notificacao = Notificacao.criarBroadcast(mensagem);
        notificacaoRepositorio.salvar(notificacao);
    }

    public void marcarComoLida(NotificacaoId id, UUID usuarioId) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da notificação não pode ser nulo.");
        }

        Notificacao notificacao = notificacaoRepositorio.obter(id);
        if (notificacao == null) {
            throw new IllegalArgumentException("Notificação não encontrada.");
        }

        notificacao.marcarComoLida(usuarioId);
        notificacaoRepositorio.atualizar(notificacao);
    }

    public List<Notificacao> obterNotificacoesDoUsuario(UUID usuarioAlvo) {
        if (usuarioAlvo == null) {
            throw new IllegalArgumentException("O usuário alvo é obrigatório para buscar notificações.");
        }
        return notificacaoRepositorio.obterPorUsuario(usuarioAlvo);
    }

    public List<Notificacao> obterNotificacoesNaoLidas(UUID usuarioAlvo) {
        if (usuarioAlvo == null) {
            throw new IllegalArgumentException("O usuário alvo é obrigatório para buscar notificações.");
        }
        return notificacaoRepositorio.obterNaoLidasPorUsuario(usuarioAlvo);
    }
}