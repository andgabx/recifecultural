package recifecultural.dominio.agenda.acessibilidade;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecursosOrdenadosTest {

    @Test
    void iteratorBuscaRecursosDoRepositorioEmPaginasDeDez() {
        UUID eventoId = UUID.randomUUID();
        RepositorioPaginadoFake repositorio = new RepositorioPaginadoFake(criarRecursos(eventoId, 12));

        Iterator<RecursoAcessibilidade> iterator = new RecursosOrdenados(repositorio, eventoId).iterator();

        List<RecursoAcessibilidade> percorridos = new ArrayList<>();
        while (iterator.hasNext()) {
            percorridos.add(iterator.next());
        }

        assertEquals(12, percorridos.size());
        assertEquals(List.of(0, 1, 2), repositorio.paginasConsultadas);
        assertEquals(List.of(10, 10, 10), repositorio.tamanhosConsultados);
    }

    @Test
    void iteratorNaoBuscaNovaPaginaEnquantoAindaHaItensNaPaginaAtual() {
        UUID eventoId = UUID.randomUUID();
        RepositorioPaginadoFake repositorio = new RepositorioPaginadoFake(criarRecursos(eventoId, 12));

        Iterator<RecursoAcessibilidade> iterator = new RecursosOrdenados(repositorio, eventoId).iterator();

        for (int i = 0; i < 9; i++) {
            assertTrue(iterator.hasNext());
            iterator.next();
        }

        assertTrue(iterator.hasNext());
        assertEquals(List.of(0), repositorio.paginasConsultadas);
        iterator.next();
        assertTrue(iterator.hasNext());
        assertEquals(List.of(0, 1), repositorio.paginasConsultadas);
    }

    @Test
    void iteratorIndicaFimQuandoRepositorioRetornaPaginaVazia() {
        UUID eventoId = UUID.randomUUID();
        RepositorioPaginadoFake repositorio = new RepositorioPaginadoFake(List.of());

        Iterator<RecursoAcessibilidade> iterator = new RecursosOrdenados(repositorio, eventoId).iterator();

        assertFalse(iterator.hasNext());
        assertEquals(List.of(0), repositorio.paginasConsultadas);
    }

    private static List<RecursoAcessibilidade> criarRecursos(UUID eventoId, int quantidade) {
        List<RecursoAcessibilidade> recursos = new ArrayList<>();
        TipoRecursoAcessibilidade[] tipos = TipoRecursoAcessibilidade.values();
        for (int i = 0; i < quantidade; i++) {
            recursos.add(new RecursoAcessibilidade(
                    UUID.randomUUID(), eventoId, tipos[i % tipos.length]));
        }
        return recursos;
    }

    private static class RepositorioPaginadoFake implements IRecursoAcessibilidadeRepositorio {
        private final List<RecursoAcessibilidade> recursos;
        private final List<Integer> paginasConsultadas = new ArrayList<>();
        private final List<Integer> tamanhosConsultados = new ArrayList<>();

        private RepositorioPaginadoFake(List<RecursoAcessibilidade> recursos) {
            this.recursos = recursos;
        }

        @Override
        public void salvar(RecursoAcessibilidade recurso) {
        }

        @Override
        public Optional<RecursoAcessibilidade> obter(UUID id) {
            return Optional.empty();
        }

        @Override
        public List<RecursoAcessibilidade> listarPorApresentacao(UUID apresentacaoId) {
            return List.of();
        }

        @Override
        public List<RecursoAcessibilidade> listarAtivosPorEvento(UUID eventoId) {
            return List.of();
        }

        @Override
        public List<RecursoAcessibilidade> listarPorEvento(UUID eventoId) {
            return List.of();
        }

        @Override
        public List<RecursoAcessibilidade> listarTodos() {
            return List.of();
        }

        @Override
        public void atualizar(RecursoAcessibilidade recurso) {
        }

        @Override
        public List<RecursoAcessibilidade> listarRecursosOrdenados(UUID eventoId, int pagina, int tamanhoPagina) {
            paginasConsultadas.add(pagina);
            tamanhosConsultados.add(tamanhoPagina);
            int inicio = pagina * tamanhoPagina;
            if (inicio >= recursos.size()) {
                return List.of();
            }
            int fim = Math.min(inicio + tamanhoPagina, recursos.size());
            return recursos.subList(inicio, fim);
        }
    }
}
