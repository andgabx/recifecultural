package recifecultural.dominio.agenda.bloqueioadministrativo;

import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class BloqueioAdministrativo {
    private final UUID id;
    private final UUID idLocal;
    private String motivo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public BloqueioAdministrativo(
            UUID idLocal,
            String motivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        this.id = UUID.randomUUID();
        this.idLocal = idLocal;
        setMotivo(motivo);
        setPeriodo(dataInicio, dataFim);
    }

    public BloqueioAdministrativo(
            UUID id,
            UUID idLocal,
            String motivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        this.id = id;
        this.idLocal = idLocal;
        this.motivo = motivo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    private void setPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) throw new IllegalArgumentException("Datas são obrigatórias.");
        if (fim.isBefore(inicio)) throw new IllegalArgumentException("Fim antes do início.");

        this.dataInicio = inicio;
        this.dataFim = fim;
    }

    public void setMotivo(String motivo) {
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo é obrigatório.");
        }
        this.motivo = motivo;
    }
}