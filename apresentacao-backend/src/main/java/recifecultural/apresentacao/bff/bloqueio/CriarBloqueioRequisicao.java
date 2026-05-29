package recifecultural.apresentacao.bff.bloqueio;

import java.time.LocalDate;
import java.util.UUID;

public record CriarBloqueioRequisicao(UUID espacoId, LocalDate inicio, LocalDate fim, String justificativa) {}
