package recifecultural.dominio.agenda.comentario;

import recifecultural.dominio.agenda.BilheteriaDigital;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class ComentarioService {

    private final ComentarioRepositorio repositorio;

    public ComentarioService(ComentarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public void postar(Comentario comentario) {
        repositorio.salvar(comentario);
    }

    public void postarComNota(Comentario comentario, BilheteriaDigital bilheteria) {
        if (!bilheteria.verificarPresenca(comentario.getEspectadorId(), comentario.getEventoId()))
            throw new IllegalStateException("Espectador não esteve presente no evento.");
        repositorio.salvar(comentario);
    }

    public void curtir(UUID comentarioId, UUID espectadorId) {
        Comentario comentario = buscarOuLancar(comentarioId);
        comentario.curtir(espectadorId);
        repositorio.atualizar(comentario);
    }

    public void responder(UUID comentarioPaiId, Comentario resposta) {
        buscarOuLancar(comentarioPaiId);
        repositorio.salvar(resposta);
    }

    public void editar(UUID id, String novoTexto) {
        Comentario comentario = buscarOuLancar(id);
        comentario.editar(novoTexto);
        repositorio.atualizar(comentario);
    }

    public void deletar(UUID id) {
        Comentario comentario = buscarOuLancar(id);
        comentario.deletar();
        repositorio.atualizar(comentario);
    }

    public List<Comentario> listarAtivos(UUID eventoId) {
        return repositorio.listarPorEvento(eventoId).stream()
                .filter(c -> c.getStatus() == StatusComentario.ATIVO)
                .collect(Collectors.toList());
    }

    public Optional<Comentario> obter(UUID id) {
        return repositorio.obter(id);
    }

    private Comentario buscarOuLancar(UUID id) {
        return repositorio.obter(id)
                .orElseThrow(() -> new IllegalArgumentException("Comentário não encontrado: " + id));
    }
}
