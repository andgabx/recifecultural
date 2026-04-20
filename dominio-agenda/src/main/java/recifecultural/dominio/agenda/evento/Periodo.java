package recifecultural.dominio.agenda.evento;

import java.time.LocalDateTime;

public final class Periodo {
    private final LocalDateTime inicio;
    private final LocalDateTime fim;

    public Periodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null)
            throw new IllegalArgumentException("Datas são obrigatórias.");
        if (fim.isBefore(inicio))
            throw new IllegalArgumentException("Fim antes do início.");
        this.inicio = inicio;
        this.fim = fim;
    }

    public LocalDateTime getInicio() { return inicio; }
    public LocalDateTime getFim()    { return fim; }

    public boolean contemData(LocalDateTime data) {
        return !data.isBefore(inicio) && !data.isAfter(fim);
    }
}
