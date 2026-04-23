package recifecultural.dominio.artista.produtor;

import java.util.UUID;

/**
 * Porta de saída usada pelo domínio-artista para consultar a agenda
 * sem criar dependência direta entre módulos Maven.
 * Implementada na camada de infraestrutura.
 */
public interface IEventoConsultaPort {
    boolean possuiEventosFuturosConfirmados(UUID artistaId);
}