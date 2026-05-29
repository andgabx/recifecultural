package recifecultural.apresentacao.bff.sorteio;

import java.time.LocalDateTime;
import java.util.UUID;

public record CriarSorteioRequisicao(
        UUID apresentacaoId,
        UUID eventoId,
        int vagas,
        LocalDateTime prazoInscricao,
        LocalDateTime dataApresentacao) {}
