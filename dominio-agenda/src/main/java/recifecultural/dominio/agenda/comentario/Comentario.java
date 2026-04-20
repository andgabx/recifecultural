package recifecultural.dominio.agenda.comentario;

import recifecultural.dominio.agenda.espectador.EspectadorId;
import recifecultural.dominio.agenda.evento.EventoId;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Comentario {

    private static final int TEXTO_MINIMO = 10;
    private static final int TEXTO_MAXIMO = 500;

    private final ComentarioId id;
    private final EspectadorId espectadorId;
    private final EventoId eventoId;
    private final ComentarioId comentarioPaiId;
    private final LocalDateTime criadoEm;

    private String texto;
    private Nota nota;
    private StatusComentario status;
    private final Set<EspectadorId> curtidas;

    public Comentario(ComentarioId id, EspectadorId espectadorId, EventoId eventoId, String texto) {
        this(id, espectadorId, eventoId, texto, null, null);
    }

    public Comentario(ComentarioId id, EspectadorId espectadorId, EventoId eventoId, String texto, ComentarioId comentarioPaiId) {
        this(id, espectadorId, eventoId, texto, null, comentarioPaiId);
    }

    public Comentario(ComentarioId id, EspectadorId espectadorId, EventoId eventoId, String texto, Nota nota, ComentarioId comentarioPaiId) {
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

    public void curtir(EspectadorId espectadorId) {
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

    public ComentarioId getId() { return id; }
    public EspectadorId getEspectadorId() { return espectadorId; }
    public EventoId getEventoId() { return eventoId; }
    public ComentarioId getComentarioPaiId() { return comentarioPaiId; }
    public String getTexto() { return texto; }
    public Nota getNota() { return nota; }
    public StatusComentario getStatus() { return status; }
    public Set<EspectadorId> getCurtidas() { return Set.copyOf(curtidas); }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
