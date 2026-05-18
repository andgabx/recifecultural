package recifecultural.dominio.compartilhado.auditoria;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro imutável de uma ação auditável.
 *
 * Gerado pelo Decorator {@code EventoRepositorioComAuditoria} (Par 4):
 * cada transição de status, criação ou remoção de evento vira um registro.
 * O agregado é simples e write-once — não há método de mutação.
 */
public class RegistroAuditoria {

    private final UUID id;
    private final String entidade;
    private final UUID entidadeId;
    private final AcaoAuditoria acao;
    private final String statusAnterior;
    private final String statusNovo;
    private final String descricao;
    private final LocalDateTime momento;

    public RegistroAuditoria(String entidade, UUID entidadeId, AcaoAuditoria acao,
                             String statusAnterior, String statusNovo, String descricao) {
        if (entidade == null || entidade.isBlank())
            throw new IllegalArgumentException("Entidade é obrigatória.");
        if (entidadeId == null)
            throw new IllegalArgumentException("ID da entidade é obrigatório.");
        if (acao == null)
            throw new IllegalArgumentException("Ação é obrigatória.");

        this.id = UUID.randomUUID();
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.acao = acao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.descricao = descricao;
        this.momento = LocalDateTime.now();
    }

    public RegistroAuditoria(UUID id, String entidade, UUID entidadeId, AcaoAuditoria acao,
                             String statusAnterior, String statusNovo, String descricao,
                             LocalDateTime momento) {
        this.id = id;
        this.entidade = entidade;
        this.entidadeId = entidadeId;
        this.acao = acao;
        this.statusAnterior = statusAnterior;
        this.statusNovo = statusNovo;
        this.descricao = descricao;
        this.momento = momento;
    }

    public UUID getId() { return id; }
    public String getEntidade() { return entidade; }
    public UUID getEntidadeId() { return entidadeId; }
    public AcaoAuditoria getAcao() { return acao; }
    public String getStatusAnterior() { return statusAnterior; }
    public String getStatusNovo() { return statusNovo; }
    public String getDescricao() { return descricao; }
    public LocalDateTime getMomento() { return momento; }
}
