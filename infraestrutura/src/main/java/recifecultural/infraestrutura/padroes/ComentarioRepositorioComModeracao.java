package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;
import recifecultural.dominio.agenda.comentario.StatusComentario;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/*
 * Padrão Decorator (Par 4): decora ComentarioRepositorio aplicando moderação
 * textual antes de persistir. Substitui palavras proibidas por asteriscos sem
 * o ComentarioService precisar conhecer a lista de termos vetados.
 */
public class ComentarioRepositorioComModeracao implements ComentarioRepositorio {

    private static final Set<String> PALAVRAS_VETADAS = Set.of(
            "spam", "fraude", "golpe", "scam"
    );

    private final ComentarioRepositorio delegado;

    public ComentarioRepositorioComModeracao(ComentarioRepositorio delegado) {
        if (delegado == null) throw new IllegalArgumentException("Delegado não pode ser nulo.");
        this.delegado = delegado;
    }

    @Override
    public void salvar(Comentario comentario) {
        // Guarda o texto original para poder restaurar se o delegate falhar (Bug 2)
        String textoOriginal = comentario.getTexto();
        moderar(comentario);
        try {
            delegado.salvar(comentario);
        } catch (RuntimeException e) {
            restaurar(comentario, textoOriginal);
            throw e;
        }
    }

    @Override
    public void atualizar(Comentario comentario) {
        String textoOriginal = comentario.getTexto();
        moderar(comentario);
        try {
            delegado.atualizar(comentario);
        } catch (RuntimeException e) {
            restaurar(comentario, textoOriginal);
            throw e;
        }
    }

    private void moderar(Comentario comentario) {
        // Bug 1: só modera comentários ATIVOS — editar() lança exceção em qualquer outro status
        if (comentario.getStatus() != StatusComentario.ATIVO) return;

        String texto = comentario.getTexto();
        if (texto == null) return;

        String censurado = texto;
        for (String palavra : PALAVRAS_VETADAS) {
            String mascara = "*".repeat(palavra.length());
            // Bug 3: Pattern.quote() garante que a palavra é tratada como literal,
            // não como expressão regular (evita crash se a palavra tiver '.', '+', etc.)
            censurado = censurado.replaceAll("(?i)" + Pattern.quote(palavra), mascara);
        }
        if (!censurado.equals(texto)) {
            comentario.editar(censurado);
        }
    }

    // Bug 2: desfaz a mutação em memória se o delegate lançar exceção
    private void restaurar(Comentario comentario, String textoOriginal) {
        if (comentario.getStatus() == StatusComentario.ATIVO
                && !textoOriginal.equals(comentario.getTexto())) {
            comentario.editar(textoOriginal);
        }
    }

    @Override
    public Optional<Comentario> obter(UUID id) {
        return delegado.obter(id);
    }

    @Override
    public void deletar(UUID id) {
        delegado.deletar(id);
    }

    @Override
    public List<Comentario> listarPorEvento(UUID eventoId) {
        return delegado.listarPorEvento(eventoId);
    }

    @Override
    public boolean existeNotaPorEspectador(UUID espectadorId, UUID eventoId) {
        return delegado.existeNotaPorEspectador(espectadorId, eventoId);
    }
}
