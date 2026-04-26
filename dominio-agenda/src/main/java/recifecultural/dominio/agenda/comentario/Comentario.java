package recifecultural.dominio.agenda.comentario;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Comentario {

    private static final int TEXTO_MINIMO = 10;
    private static final int TEXTO_MAXIMO = 500;

    private final UUID id;
    private final UUID espectadorId;
    private final UUID eventoId;
    private final UUID comentarioPaiId;
    private final LocalDateTime criadoEm;

    private String texto;
    private Nota nota;
    private StatusComentario status;
    private final Set<UUID> curtidas;

    public Comentario(UUID id, UUID espectadorId, UUID eventoId, String texto) {
        this(id, espectadorId, eventoId, texto, null, null);
    }

    public Comentario(UUID id, UUID espectadorId, UUID eventoId, String texto, UUID comentarioPaiId) {
        this(id, espectadorId, eventoId, texto, null, comentarioPaiId);
    }

    public Comentario(UUID id, UUID espectadorId, UUID eventoId, String texto, Nota nota, UUID comentarioPaiId) {
        this.id = id;
        this.espectadorId = espectadorId;
        this.eventoId = eventoId;
        this.comentarioPaiId = comentarioPaiId;
        this.criadoEm = LocalDateTime.now();
        this.curtidas = new HashSet<>();
        this.status = StatusComentario.ATIVO;
        this.nota = nota;
        setTexto(texto);
    }

    public void curtir(UUID espectadorId) {
        if (this.espectadorId.equals(espectadorId))
            throw new IllegalArgumentException("Espectador não pode curtir o próprio comentário.");
        if (curtidas.contains(espectadorId))
            throw new IllegalStateException("Espectador já curtiu este comentário.");
        curtidas.add(espectadorId);
    }

    public void editar(String novoTexto) {
        exigirAtivo("editar");
        setTexto(novoTexto);
    }

    public void deletar() {
        exigirAtivo("deletar");
        this.status = StatusComentario.DELETADO;
    }

    private void setTexto(String texto) {
        if (texto == null || texto.isBlank())
            throw new IllegalArgumentException("Texto do comentário é obrigatório.");
        if (texto.trim().length() < TEXTO_MINIMO)
            throw new IllegalArgumentException("Texto deve ter no mínimo " + TEXTO_MINIMO + " caracteres.");
        if (texto.length() > TEXTO_MAXIMO)
            throw new IllegalArgumentException("Texto não pode ultrapassar " + TEXTO_MAXIMO + " caracteres.");
        this.texto = texto;
    }

    private void exigirAtivo(String acao) {
        if (this.status != StatusComentario.ATIVO)
            throw new IllegalStateException("Não é possível " + acao + " um comentário com status " + this.status + ".");
    }

    public UUID getId() { return id; }
    public UUID getEspectadorId() { return espectadorId; }
    public UUID getEventoId() { return eventoId; }
    public UUID getComentarioPaiId() { return comentarioPaiId; }
    public String getTexto() { return texto; }
    public Nota getNota() { return nota; }
    public StatusComentario getStatus() { return status; }
    public Set<UUID> getCurtidas() { return Set.copyOf(curtidas); }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
