package recifecultural.dominio.agenda;

import java.util.UUID;

public interface BilheteriaDigital {
    boolean verificarPresenca(UUID espectadorId, UUID eventoId);
}
