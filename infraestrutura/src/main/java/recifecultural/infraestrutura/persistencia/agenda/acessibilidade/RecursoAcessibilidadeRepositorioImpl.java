package recifecultural.infraestrutura.persistencia.agenda.acessibilidade;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import recifecultural.dominio.agenda.acessibilidade.IRecursoAcessibilidadeRepositorio;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidade;
import recifecultural.dominio.agenda.acessibilidade.StatusRecurso;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecursoAcessibilidadeRepositorioImpl implements IRecursoAcessibilidadeRepositorio {

    private final RecursoAcessibilidadeJpaRepository jpa;
    private final ModelMapper mapeador;

    public RecursoAcessibilidadeRepositorioImpl(RecursoAcessibilidadeJpaRepository jpa, ModelMapper mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(RecursoAcessibilidade recurso) {
        jpa.save(mapeador.map(recurso, RecursoAcessibilidadeJpa.class));
    }

    @Override
    public Optional<RecursoAcessibilidade> obter(UUID id) {
        return jpa.findById(id).map(r -> mapeador.map(r, RecursoAcessibilidade.class));
    }

    @Override
    public List<RecursoAcessibilidade> listarPorApresentacao(UUID apresentacaoId) {
        return jpa.findByApresentacaoId(apresentacaoId).stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public List<RecursoAcessibilidade> listarAtivosPorEvento(UUID eventoId) {
        return jpa.findByEventoIdAndStatus(eventoId, StatusRecurso.CONFIRMADO).stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public List<RecursoAcessibilidade> listarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public List<RecursoAcessibilidade> listarTodos() {
        return jpa.findAll().stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public List<RecursoAcessibilidade> listarRecursosOrdenados(UUID eventoId, int pagina, int tamanhoPagina) {
        if (pagina < 0) throw new IllegalArgumentException("Página não pode ser negativa.");
        if (tamanhoPagina <= 0) throw new IllegalArgumentException("Tamanho da página deve ser positivo.");

        return jpa.findRecursosOrdenados(eventoId, PageRequest.of(pagina, tamanhoPagina)).stream()
                .map(r -> mapeador.map(r, RecursoAcessibilidade.class))
                .toList();
    }

    @Override
    public void atualizar(RecursoAcessibilidade recurso) {
        jpa.save(mapeador.map(recurso, RecursoAcessibilidadeJpa.class));
    }
}
