package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.time.LocalDateTime;
import java.util.UUID;

public class BloqueioAdministrativo {
    private final BloqueioAdministrativoId id;
    private final UUID idEspaco;
    private String motivo;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;

    public BloqueioAdministrativo(
            UUID idEspaco,
            String motivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        this.id = BloqueioAdministrativoId.gerar();
        this.idEspaco = idEspaco;
        setMotivo(motivo);
        setPeriodo(dataInicio, dataFim);
    }

    public BloqueioAdministrativo(
            BloqueioAdministrativoId id,
            UUID idEspaco,
            String motivo,
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    ) {
        if (id == null) {
            throw new IllegalArgumentException("O ID do bloqueio é obrigatório.");
        }
        this.id = id;
        this.idEspaco = idEspaco;
        this.motivo = motivo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public void setPeriodo(LocalDateTime inicio, LocalDateTime fim) {
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

    public BloqueioAdministrativoId getId() { return id; }
    public UUID getIdEspaco() { return idEspaco; }
    public String getMotivo() { return motivo; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
}