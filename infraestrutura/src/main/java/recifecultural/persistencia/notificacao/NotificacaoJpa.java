package recifecultural.persistencia.notificacao;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "notificacao")
public class NotificacaoJpa {

    @Id
    private UUID id;
    private UUID usuarioAlvo;
    private String mensagem;
    private boolean foiLida;
    private boolean broadcast;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "notificacao_lida_por", joinColumns = @JoinColumn(name = "notificacao_id"))
    @Column(name = "usuario_id")
    private Set<UUID> lidaPor = new HashSet<>();

    private LocalDateTime dataCriacao;

    public NotificacaoJpa() {
    }

    public NotificacaoJpa(UUID id, UUID usuarioAlvo, String mensagem, boolean foiLida, boolean broadcast, Set<UUID> lidaPor, LocalDateTime dataCriacao) {
        this.id = id;
        this.usuarioAlvo = usuarioAlvo;
        this.mensagem = mensagem;
        this.foiLida = foiLida;
        this.broadcast = broadcast;
        this.lidaPor = lidaPor;
        this.dataCriacao = dataCriacao;
    }

    // Getters e Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getUsuarioAlvo() { return usuarioAlvo; }
    public void setUsuarioAlvo(UUID usuarioAlvo) { this.usuarioAlvo = usuarioAlvo; }
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    public boolean isFoiLida() { return foiLida; }
    public void setFoiLida(boolean foiLida) { this.foiLida = foiLida; }
    public boolean isBroadcast() { return broadcast; }
    public void setBroadcast(boolean broadcast) { this.broadcast = broadcast; }
    public Set<UUID> getLidaPor() { return lidaPor; }
    public void setLidaPor(Set<UUID> lidaPor) { this.lidaPor = lidaPor; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}