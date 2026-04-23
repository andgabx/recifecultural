package recifecultural.dominio.agenda.acessibilidade;

import java.util.UUID;

public class RecursoAcessibilidade {

    private static final int TAMANHO_MINIMO_JUSTIFICATIVA = 30;

    private final UUID id;
    private final UUID apresentacaoId;
    private final UUID eventoId;
    private final TipoRecursoAcessibilidade tipo;
    private StatusRecurso status;
    private String justificativaRemocao;

    public RecursoAcessibilidade(UUID apresentacaoId, UUID eventoId, TipoRecursoAcessibilidade tipo) {
        if (apresentacaoId == null) throw new IllegalArgumentException("Apresentação é obrigatória.");
        if (eventoId == null) throw new IllegalArgumentException("Evento é obrigatório.");
        if (tipo == null) throw new IllegalArgumentException("Tipo de recurso é obrigatório.");

        this.id = UUID.randomUUID();
        this.apresentacaoId = apresentacaoId;
        this.eventoId = eventoId;
        this.tipo = tipo;
        this.status = StatusRecurso.CONFIRMADO;
    }

    public void remover(String justificativa) {
        if (status != StatusRecurso.CONFIRMADO)
            throw new IllegalStateException("Recurso já foi removido.");
        if (justificativa == null || justificativa.isBlank())
            throw new IllegalArgumentException("Justificativa é obrigatória para remover um recurso anunciado.");
        if (justificativa.trim().length() < TAMANHO_MINIMO_JUSTIFICATIVA)
            throw new IllegalArgumentException(
                    "Justificativa deve ter no mínimo " + TAMANHO_MINIMO_JUSTIFICATIVA + " caracteres.");

        this.justificativaRemocao = justificativa;
        this.status = StatusRecurso.REMOVIDO;
    }

    public UUID getId() { return id; }
    public UUID getApresentacaoId() { return apresentacaoId; }
    public UUID getEventoId() { return eventoId; }
    public TipoRecursoAcessibilidade getTipo() { return tipo; }
    public StatusRecurso getStatus() { return status; }
    public String getJustificativaRemocao() { return justificativaRemocao; }
}
