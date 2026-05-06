package recifecultural.dominio.compartilhado.notificacao;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notificacao {
    private final NotificacaoId id;
    private UUID usuarioAlvo;
    private String mensagem;

    private String contexto;
    private UUID idReferencia;

    private boolean foiLida;
    private LocalDateTime dataCriacao;

    public Notificacao(UUID usuarioAlvo, String mensagem) {
        this(usuarioAlvo, mensagem, null, null);
    }

    public Notificacao(UUID usuarioAlvo, String mensagem, String contexto, UUID idReferencia) {
        this.id = NotificacaoId.gerar();
        setUsuarioAlvo(usuarioAlvo);
        setMensagem(mensagem);
        this.contexto = contexto;
        this.idReferencia = idReferencia;
        this.foiLida = false;
        this.dataCriacao = LocalDateTime.now();
    }

    public Notificacao(
            NotificacaoId id,
            UUID usuarioAlvo,
            String mensagem,
            String contexto,
            UUID idReferencia,
            boolean foiLida,
            LocalDateTime dataCriacao
    ) {
        if (id == null) throw new IllegalArgumentException("O ID da notificação é obrigatório.");
        if (dataCriacao == null) throw new IllegalArgumentException("A data de criação é obrigatória.");

        this.id = id;
        setUsuarioAlvo(usuarioAlvo);
        setMensagem(mensagem);
        this.contexto = contexto;
        this.idReferencia = idReferencia;
        this.foiLida = foiLida;
        this.dataCriacao = dataCriacao;
    }

    private void setUsuarioAlvo(UUID usuarioAlvo) {
        if (usuarioAlvo == null) {
            throw new IllegalArgumentException("O usuário alvo é obrigatório para as notificações.");
        }
        this.usuarioAlvo = usuarioAlvo;
    }

    private void setMensagem(String mensagem) {
        if (mensagem == null || mensagem.isBlank()) {
            throw new IllegalArgumentException("A mensagem da notificação é obrigatória.");
        }
        this.mensagem = mensagem;
    }

    public void marcarComoLida() {
        this.foiLida = true;
    }

    public void marcarComoNaoLida() {
        this.foiLida = false;
    }

    // Getters
    public NotificacaoId getId() { return id; }
    public UUID getUsuarioAlvo() { return usuarioAlvo; }
    public String getMensagem() { return mensagem; }
    public String getContexto() { return contexto; }
    public UUID getIdReferencia() { return idReferencia; }
    public boolean isFoiLida() { return foiLida; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}