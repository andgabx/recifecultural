package recifecultural.dominio.espaco.suporte;

import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import java.util.UUID;
import java.time.LocalDateTime;

public class ChamadoSuporte {
    private final UUID id;
    private final UUID assentoId;
    private final String descricao;
    private final MotivoIndisponibilidadeAssento motivo;
    private StatusChamado status;
    private LocalDateTime dataAbertura;

    public ChamadoSuporte(UUID assentoId, MotivoIndisponibilidadeAssento motivo, String descricao) {
        if (assentoId == null) throw new IllegalArgumentException("Assento ID é obrigatório.");
        if (motivo == null) throw new IllegalArgumentException("Motivo é obrigatório.");
        if (descricao == null || descricao.isBlank()) throw new IllegalArgumentException("Descrição é obrigatória.");
        
        this.id = UUID.randomUUID();
        this.assentoId = assentoId;
        this.motivo = motivo;
        this.descricao = descricao;
        this.status = StatusChamado.ABERTO;
        this.dataAbertura = LocalDateTime.now();
    }

    public void resolver() {
        if (this.status == StatusChamado.RESOLVIDO) {
            throw new IllegalStateException("O chamado já está resolvido.");
        }
        this.status = StatusChamado.RESOLVIDO;
    }

    public UUID getId() { return id; }
    public UUID getAssentoId() { return assentoId; }
    public MotivoIndisponibilidadeAssento getMotivo() { return motivo; }
    public String getDescricao() { return descricao; }
    public StatusChamado getStatus() { return status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }
}
