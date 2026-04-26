package recifecultural.dominio.financeiro;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public final class Periodo {

    private final LocalDateTime dataInicio;
    private final LocalDateTime dataFim;

    public Periodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        notNull(dataInicio, "A data de início não pode ser nula.");
        notNull(dataFim, "A data de fim não pode ser nula.");
        isTrue(dataFim.isAfter(dataInicio), "A data de fim deve ser posterior à data de início.");
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public boolean contem(LocalDateTime dataHora) {
        return !dataHora.isBefore(dataInicio) && !dataHora.isAfter(dataFim);
    }

    public boolean sobrepoe(Periodo outro) {
        return dataInicio.isBefore(outro.dataFim) && dataFim.isAfter(outro.dataInicio);
    }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
}
