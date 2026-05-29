package recifecultural.aplicacao.agenda.evento;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Gera um UUID determinístico (v3, MD5) a partir do par (eventoId, dataIso).
 * Garantia: mesma data + mesmo evento → mesmo apresentacaoId, sempre.
 *
 * Mantém o domínio agnóstico: a regra de negócio continua tratando
 * apresentacaoId como UUID opaco, mas o cálculo está aqui na aplicação
 * porque é uma decisão de fronteira (BFF expõe IDs estáveis para o front).
 */
public final class ApresentacaoIdHelper {

    private ApresentacaoIdHelper() {}

    public static UUID gerar(UUID eventoId, String dataIso) {
        if (eventoId == null || dataIso == null) {
            throw new IllegalArgumentException("eventoId e dataIso são obrigatórios.");
        }
        String seed = eventoId.toString() + "|" + dataIso;
        return UUID.nameUUIDFromBytes(seed.getBytes(StandardCharsets.UTF_8));
    }
}
