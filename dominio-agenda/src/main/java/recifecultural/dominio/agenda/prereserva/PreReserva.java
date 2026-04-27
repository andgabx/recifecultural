package recifecultural.dominio.agenda.prereserva;

import java.time.LocalDateTime;
import java.util.UUID;

public class PreReserva {

    private final PreReservaId id;
    private final UUID assentoId;
    private final UUID setorId;
    private final UUID usuarioId;
    private final LocalDateTime criadaEm;
    private final LocalDateTime expiraEm;
    private StatusPreReserva status;
    private int versao;

    public PreReserva(UUID assentoId, UUID setorId, UUID usuarioId,
                      DuracaoPreReserva duracao, LocalDateTime agora) {
        if (assentoId == null) throw new IllegalArgumentException("Assento é obrigatório.");
        if (setorId == null) throw new IllegalArgumentException("Setor é obrigatório.");
        if (usuarioId == null) throw new IllegalArgumentException("Usuário é obrigatório.");
        if (duracao == null) throw new IllegalArgumentException("Duração é obrigatória.");

        this.id = PreReservaId.novo();
        this.assentoId = assentoId;
        this.setorId = setorId;
        this.usuarioId = usuarioId;
        this.criadaEm = agora;
        this.expiraEm = agora.plus(duracao.valor());
        this.status = StatusPreReserva.ATIVA;
        this.versao = 0;
    }

    public PreReserva(PreReservaId id, UUID assentoId, UUID setorId, UUID usuarioId,
                      LocalDateTime criadaEm, LocalDateTime expiraEm,
                      StatusPreReserva status, int versao) {
        this.id = id; this.assentoId = assentoId; this.setorId = setorId;
        this.usuarioId = usuarioId; this.criadaEm = criadaEm; this.expiraEm = expiraEm;
        this.status = status; this.versao = versao;
    }

    public boolean estaExpirada(LocalDateTime agora) {
        return agora.isAfter(expiraEm);
    }

    public void expirar(LocalDateTime agora) {
        if (this.status != StatusPreReserva.ATIVA)
            throw new IllegalStateException("Somente pré-reservas ativas podem ser expiradas.");
        if (!estaExpirada(agora))
            throw new IllegalStateException("Pré-reserva ainda não atingiu o tempo de expiração.");
        this.status = StatusPreReserva.EXPIRADA;
    }

    public void cancelar() {
        if (this.status != StatusPreReserva.ATIVA)
            throw new IllegalStateException("Somente pré-reservas ativas podem ser canceladas.");
        this.status = StatusPreReserva.CANCELADA;
    }

    public void confirmar() {
        if (this.status != StatusPreReserva.ATIVA)
            throw new IllegalStateException("Somente pré-reservas ativas podem ser confirmadas.");
        this.status = StatusPreReserva.CONVERTIDA;
    }

    public PreReservaId getId() { return id; }
    public UUID getAssentoId() { return assentoId; }
    public UUID getSetorId() { return setorId; }
    public UUID getUsuarioId() { return usuarioId; }
    public LocalDateTime getCriadaEm() { return criadaEm; }
    public LocalDateTime getExpiraEm() { return expiraEm; }
    public StatusPreReserva getStatus() { return status; }
    public int getVersao() { return versao; }
}