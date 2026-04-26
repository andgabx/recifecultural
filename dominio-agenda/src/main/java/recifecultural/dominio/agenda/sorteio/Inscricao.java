package recifecultural.dominio.agenda.sorteio;

import java.time.LocalDateTime;
import java.util.UUID;

public class Inscricao {
    private final UUID espectadorId;
    private final LocalDateTime momentoInscricao;
    private StatusInscricao status;

    public Inscricao(UUID espectadorId, LocalDateTime momentoInscricao) {
        if (espectadorId == null) throw new IllegalArgumentException("Espectador é obrigatório.");
        if (momentoInscricao == null) throw new IllegalArgumentException("Momento da inscrição é obrigatório.");
        this.espectadorId = espectadorId;
        this.momentoInscricao = momentoInscricao;
        this.status = StatusInscricao.INSCRITO;
    }

    public UUID getEspectadorId() { return espectadorId; }
    public LocalDateTime getMomentoInscricao() { return momentoInscricao; }
    public StatusInscricao getStatus() { return status; }

    void marcarComo(StatusInscricao novoStatus) {
        this.status = novoStatus;
    }
}
