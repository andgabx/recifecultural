package recifecultural.dominio.espaco.suporte;

import java.time.LocalDateTime;
import java.util.UUID;

import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;

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

    public ChamadoSuporte(UUID id, UUID assentoId, MotivoIndisponibilidadeAssento motivo,
                          String descricao, StatusChamado status, LocalDateTime dataAbertura) {
        this.id = id;
        this.assentoId = assentoId;
        this.motivo = motivo;
        this.descricao = descricao;
        this.status = status;
        this.dataAbertura = dataAbertura;
    }

    public AbertoEvento eventoAbertura() {
        return new AbertoEvento(this);
    }

    public void resolver() {
        if (this.status == StatusChamado.RESOLVIDO) {
            throw new IllegalStateException("O chamado já está resolvido.");
        }
        this.status = StatusChamado.RESOLVIDO;
    }

    public void escalar() {
        if (this.status == StatusChamado.RESOLVIDO) {
            throw new IllegalStateException("Chamado já resolvido não pode ser escalado.");
        }
        if (this.status == StatusChamado.ESCALADO) {
            throw new IllegalStateException("O chamado já está escalado.");
        }
        this.status = StatusChamado.ESCALADO;
    }

    public UUID getId() { return id; }
    public UUID getAssentoId() { return assentoId; }
    public MotivoIndisponibilidadeAssento getMotivo() { return motivo; }
    public String getDescricao() { return descricao; }
    public StatusChamado getStatus() { return status; }
    public LocalDateTime getDataAbertura() { return dataAbertura; }

    public static class ChamadoEvento {
        private final ChamadoSuporte chamado;

        private ChamadoEvento(ChamadoSuporte chamado) {
            this.chamado = chamado;
        }

        public ChamadoSuporte getChamado() {
            return chamado;
        }
    }

    public static class AbertoEvento extends ChamadoEvento {
        private AbertoEvento(ChamadoSuporte chamado) {
            super(chamado);
        }
    }
}
