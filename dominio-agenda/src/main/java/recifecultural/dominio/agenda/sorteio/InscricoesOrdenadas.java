package recifecultural.dominio.agenda.sorteio;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/*
 * Padrão Iterator: percorre inscrições de um sorteio em ordem de prioridade
 * (GANHADOR → SUPLENTE → INSCRITO → outros), abstraindo a estrutura interna
 * da coleção e lendo os itens do repositório em páginas.
 */
public class InscricoesOrdenadas implements Iterable<Inscricao> {

    public static final int TAMANHO_PADRAO_PAGINA = 10;

    private final ISorteioRepositorio repositorio;
    private final UUID sorteioId;
    private final int tamanhoPagina;

    public InscricoesOrdenadas(ISorteioRepositorio repositorio, UUID sorteioId) {
        this(repositorio, sorteioId, TAMANHO_PADRAO_PAGINA);
    }

    public InscricoesOrdenadas(ISorteioRepositorio repositorio, UUID sorteioId, int tamanhoPagina) {
        if (repositorio == null) throw new IllegalArgumentException("Repositório é obrigatório.");
        if (sorteioId == null) throw new IllegalArgumentException("Sorteio é obrigatório.");
        if (tamanhoPagina <= 0) throw new IllegalArgumentException("Tamanho da página deve ser positivo.");

        this.repositorio = repositorio;
        this.sorteioId = sorteioId;
        this.tamanhoPagina = tamanhoPagina;
    }

    @Override
    public Iterator<Inscricao> iterator() {
        return new IteradorPrioridade(repositorio, sorteioId, tamanhoPagina);
    }

    private static class IteradorPrioridade implements Iterator<Inscricao> {
        private final ISorteioRepositorio repositorio;
        private final UUID sorteioId;
        private final int tamanhoPagina;
        private List<Inscricao> paginaAtual = List.of();
        private int pagina = 0;
        private int posicaoNaPagina = 0;
        private boolean fim;

        IteradorPrioridade(ISorteioRepositorio repositorio, UUID sorteioId, int tamanhoPagina) {
            this.repositorio = repositorio;
            this.sorteioId = sorteioId;
            this.tamanhoPagina = tamanhoPagina;
        }

        @Override
        public boolean hasNext() {
            return carregarPaginaSeNecessario();
        }

        @Override
        public Inscricao next() {
            if (!hasNext()) throw new NoSuchElementException();
            return paginaAtual.get(posicaoNaPagina++);
        }

        private boolean carregarPaginaSeNecessario() {
            if (posicaoNaPagina < paginaAtual.size()) {
                return true;
            }
            if (fim) {
                return false;
            }

            paginaAtual = repositorio.listarInscricoesOrdenadas(sorteioId, pagina, tamanhoPagina);
            pagina++;
            posicaoNaPagina = 0;

            if (paginaAtual.isEmpty()) {
                fim = true;
                return false;
            }
            return true;
        }
    }
}
