package recifecultural.aplicacao.agenda.comentario;

import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.ComentarioService;
import recifecultural.dominio.agenda.comentario.StatusComentario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class ComentarioServicoAplicacao {

    private final ComentarioService servico;

    public ComentarioServicoAplicacao(ComentarioService servico) {
        notNull(servico, "ComentarioService não pode ser nulo.");
        this.servico = servico;
    }

    public List<ComentarioResumo> listarPorEvento(UUID eventoId) {
        notNull(eventoId, "eventoId é obrigatório.");
        return servico.listarAtivos(eventoId).stream()
                .map(ComentarioResumo::de)
                .toList();
    }

    public UUID postar(UUID eventoId, UUID espectadorId, String texto, UUID comentarioPaiId) {
        notNull(eventoId, "eventoId é obrigatório.");
        notNull(espectadorId, "espectadorId é obrigatório.");
        notBlank(texto, "Texto é obrigatório.");

        Comentario c = new Comentario(UUID.randomUUID(), espectadorId, eventoId, texto, null, comentarioPaiId);
        if (comentarioPaiId != null) {
            servico.responder(comentarioPaiId, c);
        } else {
            servico.postar(c);
        }
        return c.getId();
    }

    public void curtir(UUID comentarioId, UUID espectadorId) {
        notNull(comentarioId, "comentarioId é obrigatório.");
        notNull(espectadorId, "espectadorId é obrigatório.");
        servico.curtir(comentarioId, espectadorId);
    }

    public void deletar(UUID comentarioId) {
        notNull(comentarioId, "comentarioId é obrigatório.");
        servico.deletar(comentarioId);
    }

    public record ComentarioResumo(
            UUID id,
            UUID espectadorId,
            UUID eventoId,
            UUID comentarioPaiId,
            String texto,
            Integer nota,
            String status,
            int totalCurtidas,
            LocalDateTime criadoEm
    ) {
        public static ComentarioResumo de(Comentario c) {
            return new ComentarioResumo(
                    c.getId(),
                    c.getEspectadorId(),
                    c.getEventoId(),
                    c.getComentarioPaiId(),
                    c.getTexto(),
                    c.getNota() != null ? c.getNota().getValor() : null,
                    c.getStatus() != null ? c.getStatus().name() : StatusComentario.ATIVO.name(),
                    c.getCurtidas().size(),
                    c.getCriadoEm()
            );
        }
    }
}
