package recifecultural.dominio.agenda.notificacao;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Notificacao {
    private final NotificacaoId id;
    private UUID usuarioAlvo;
    private String mensagem;
    private boolean foiLida;
    private boolean broadcast;
    private Set<UUID> lidaPor;
    private LocalDateTime dataCriacao;

    public Notificacao(
            UUID usuarioAlvo,
            String mensagem
    ) {
        this.id = NotificacaoId.gerar();
        setUsuarioAlvo(usuarioAlvo);
        setMensagem(mensagem);
        this.foiLida = false;
        this.broadcast = false;
        this.lidaPor = new HashSet<>();
        this.dataCriacao = LocalDateTime.now();
    }

    private Notificacao(String mensagem, boolean broadcast) {
        this.id = NotificacaoId.gerar();
        setMensagem(mensagem);
        this.usuarioAlvo = null;
        this.foiLida = false;
        this.broadcast = broadcast;
        this.lidaPor = new HashSet<>();
        this.dataCriacao = LocalDateTime.now();
    }

    public static Notificacao criarBroadcast(String mensagem) {
        return new Notificacao(mensagem, true);
    }

    public Notificacao(
            NotificacaoId id,
            UUID usuarioAlvo,
            String mensagem,
            boolean foiLida,
            boolean broadcast,
            Set<UUID> lidaPor,
            LocalDateTime dataCriacao
    ) {
        if (id == null) {
            throw new IllegalArgumentException("O ID da notificação é obrigatório.");
        }
        if (dataCriacao == null) {
            throw new IllegalArgumentException("A data de criação é obrigatória.");
        }

        this.id = id;
        this.usuarioAlvo = usuarioAlvo;
        setMensagem(mensagem);
        this.foiLida = foiLida;
        this.broadcast = broadcast;
        this.lidaPor = lidaPor != null ? lidaPor : new HashSet<>();
        this.dataCriacao = dataCriacao;
    }

    private void setUsuarioAlvo(UUID usuarioAlvo) {
        if (usuarioAlvo == null && !this.broadcast) {
            throw new IllegalArgumentException("O usuário alvo é obrigatório para notificações diretas.");
        }
        this.usuarioAlvo = usuarioAlvo;
    }

    private void setMensagem(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem da notificação é obrigatória.");
        }
        this.mensagem = mensagem;
    }

    public void marcarComoLida(UUID usuarioId) {
        if (this.broadcast) {
            if (usuarioId == null) throw new IllegalArgumentException("ID do usuário é necessário para marcar broadcast como lido.");
            this.lidaPor.add(usuarioId);
        } else {
            this.foiLida = true;
        }
    }

    public boolean isLidaPor(UUID usuarioId) {
        if (this.broadcast) {
            return this.lidaPor.contains(usuarioId);
        }
        return this.foiLida;
    }

    // Getters
    public NotificacaoId getId() { return id; }
    public UUID getUsuarioAlvo() { return usuarioAlvo; }
    public String getMensagem() { return mensagem; }
    public boolean isFoiLida() { return foiLida; }
    public boolean isBroadcast() { return broadcast; }
    public Set<UUID> getLidaPor() { return lidaPor; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}