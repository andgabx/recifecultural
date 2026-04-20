package recifecultural.dominio.agenda;

import recifecultural.dominio.agenda.espectador.EspectadorId;
import recifecultural.dominio.agenda.evento.EventoId;

import java.util.UUID;

public interface BilheteriaDigital {
    boolean verificarPresenca(EspectadorId espectadorId, EventoId eventoId);
}

