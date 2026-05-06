package recifecultural.dominio.compartilhado.notificacao;

import java.util.List;
import java.util.UUID;

public interface IUsuarioContextoServico {
    List<UUID> obterUsuariosPorContexto(String contexto, UUID idReferencia);
}