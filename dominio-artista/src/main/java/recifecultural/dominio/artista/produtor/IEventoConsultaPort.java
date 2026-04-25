package recifecultural.dominio.artista.produtor;

import java.util.UUID;

public interface IEventoConsultaPort {
    boolean possuiEventosFuturosConfirmados(UUID artistaId);
}