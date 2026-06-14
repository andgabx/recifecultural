package recifecultural.dominio.agenda.sorteio;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscricoesOrdenadasTest {

    @Test
    void iteratorBuscaInscricoesDoRepositorioEmPaginasDeDez() {
        UUID sorteioId = UUID.randomUUID();
        RepositorioPaginadoFake repositorio = new RepositorioPaginadoFake(criarInscricoes(12));

        Iterator<Inscricao> iterator = new InscricoesOrdenadas(repositorio, sorteioId).iterator();

        List<Inscricao> percorridas = new ArrayList<>();
        while (iterator.hasNext()) {
            percorridas.add(iterator.next());
        }

        assertEquals(12, percorridas.size());
        assertEquals(List.of(0, 1, 2), repositorio.paginasConsultadas);
        assertEquals(List.of(10, 10, 10), repositorio.tamanhosConsultados);
    }

    @Test
    void iteratorNaoBuscaNovaPaginaEnquantoAindaHaItensNaPaginaAtual() {
        UUID sorteioId = UUID.randomUUID();
        RepositorioPaginadoFake repositorio = new RepositorioPaginadoFake(criarInscricoes(12));

        Iterator<Inscricao> iterator = new InscricoesOrdenadas(repositorio, sorteioId).iterator();

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
        UUID sorteioId = UUID.randomUUID();
        RepositorioPaginadoFake repositorio = new RepositorioPaginadoFake(List.of());

        Iterator<Inscricao> iterator = new InscricoesOrdenadas(repositorio, sorteioId).iterator();

        assertFalse(iterator.hasNext());
        assertEquals(List.of(0), repositorio.paginasConsultadas);
    }

    private static List<Inscricao> criarInscricoes(int quantidade) {
        List<Inscricao> inscricoes = new ArrayList<>();
        for (int i = 0; i < quantidade; i++) {
            inscricoes.add(new Inscricao(UUID.randomUUID(), LocalDateTime.now().plusMinutes(i)));
        }
        return inscricoes;
    }

    private static class RepositorioPaginadoFake implements ISorteioRepositorio {
        private final List<Inscricao> inscricoes;
        private final List<Integer> paginasConsultadas = new ArrayList<>();
        private final List<Integer> tamanhosConsultados = new ArrayList<>();

        private RepositorioPaginadoFake(List<Inscricao> inscricoes) {
            this.inscricoes = inscricoes;
        }

        @Override
        public void salvar(Sorteio sorteio) {
        }

        @Override
        public Optional<Sorteio> obter(UUID id) {
            return Optional.empty();
        }

        @Override
        public void atualizar(Sorteio sorteio) {
        }

        @Override
        public void deletar(UUID id) {
        }

        @Override
        public List<Inscricao> listarInscricoesOrdenadas(UUID sorteioId, int pagina, int tamanhoPagina) {
            paginasConsultadas.add(pagina);
            tamanhosConsultados.add(tamanhoPagina);
            int inicio = pagina * tamanhoPagina;
            if (inicio >= inscricoes.size()) {
                return List.of();
            }
            int fim = Math.min(inicio + tamanhoPagina, inscricoes.size());
            return inscricoes.subList(inicio, fim);
        }
    }
}
