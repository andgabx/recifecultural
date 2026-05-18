package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
        moderar(comentario);
        delegado.salvar(comentario);
    }

    @Override
    public void atualizar(Comentario comentario) {
        moderar(comentario);
        delegado.atualizar(comentario);
    }

    private void moderar(Comentario comentario) {
        String texto = comentario.getTexto();
        if (texto == null) return;
        String censurado = texto;
        for (String palavra : PALAVRAS_VETADAS) {
            String mascara = "*".repeat(palavra.length());
            censurado = censurado.replaceAll("(?i)" + palavra, mascara);
        }
        if (!censurado.equals(texto)) {
            comentario.editar(censurado);
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
