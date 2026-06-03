package recifecultural.infraestrutura.persistencia.agenda.comentario;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.ComentarioRepositorio;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ComentarioRepositorioImpl implements ComentarioRepositorio {

    private final ComentarioJpaRepository jpa;
    private final ComentarioMapeador mapeador;

    public ComentarioRepositorioImpl(ComentarioJpaRepository jpa, ComentarioMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Comentario comentario) {
        jpa.save(mapeador.toJpa(comentario));
    }

    @Override
    public Optional<Comentario> obter(UUID id) {
        return jpa.findById(id).map(mapeador::toDominio);
    }

    @Override
    public void atualizar(Comentario comentario) {
        jpa.save(mapeador.toJpa(comentario));
    }

    @Override
    public void deletar(UUID id) {
        jpa.deleteById(id);
    }

    @Override
    public List<Comentario> listarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .map(mapeador::toDominio)
                .toList();
    }

    @Override
    public boolean existeNotaPorEspectador(UUID espectadorId, UUID eventoId) {
        return jpa.existeNotaPorEspectador(espectadorId, eventoId);
    }
}
