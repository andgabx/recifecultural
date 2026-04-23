package recifecultural.dominio.agenda.prereserva;

import java.time.Duration;

public record DuracaoPreReserva(Duration valor) {

    private static final Duration MINIMO = Duration.ofMinutes(1);
    private static final Duration MAXIMO = Duration.ofMinutes(15);
    public static final DuracaoPreReserva PADRAO = new DuracaoPreReserva(Duration.ofMinutes(10));

    public DuracaoPreReserva {
        if (valor == null) throw new IllegalArgumentException("Duração é obrigatória.");
        if (valor.compareTo(MINIMO) < 0) throw new IllegalArgumentException("Duração mínima: 1 minuto.");
        if (valor.compareTo(MAXIMO) > 0) throw new IllegalArgumentException("Duração máxima: 15 minutos.");
    }
}