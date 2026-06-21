package recifecultural.dominio.espaco.suporte;

import java.time.LocalDateTime;
import java.util.UUID;

import recifecultural.dominio.espaco.setor.MotivoIndisponibilidade;
import recifecultural.dominio.espaco.suporte.TipoChamado;

public class ChamadoSuporte {
    private final UUID id;
    private final UUID assentoId;
    private TipoChamado tipo;
    private final MotivoIndisponibilidade motivo;
    private final String descricao;

    private StatusChamado status;
    private String tecnicoResponsavel;
    private String solucaoAplicada;

    private final LocalDateTime dataAbertura;
    private LocalDateTime dataInicioAtendimento;
    private LocalDateTime dataResolucao;


    public ChamadoSuporte(UUID assentoId, TipoChamado tipo, MotivoIndisponibilidade motivo, String descricao) {
        this.id = UUID.randomUUID();
        this.assentoId = assentoId;
        this.tipo = tipo;
        this.motivo = motivo;
        this.descricao = descricao;
        this.status = StatusChamado.ABERTO;
        this.dataAbertura = LocalDateTime.now();
    }

    public ChamadoSuporte(UUID id, UUID assentoId, MotivoIndisponibilidade motivo, String descricao, StatusChamado status, LocalDateTime dataAbertura, TipoChamado tipo) {
        this.id = UUID.randomUUID();
        this.assentoId = assentoId;
        this.motivo = motivo;
        this.descricao = descricao;
        this.status = StatusChamado.ABERTO;
        this.dataAbertura = LocalDateTime.now();
    }

    public AbertoEvento eventoAbertura() {
        return new AbertoEvento(this);
    }

    public void aceitarChamado(String nomeTecnico) {
        if (this.status != StatusChamado.ABERTO && this.status != StatusChamado.ESCALADO) {
            throw new IllegalStateException("Apenas chamados abertos ou escalados podem ser aceitos.");
        }
        this.status = StatusChamado.EM_ANDAMENTO;
        this.tecnicoResponsavel = nomeTecnico;
        this.dataInicioAtendimento = LocalDateTime.now();
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
    public TipoChamado getTipo() { return tipo; }
    public MotivoIndisponibilidade getMotivo() { return motivo; }
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
