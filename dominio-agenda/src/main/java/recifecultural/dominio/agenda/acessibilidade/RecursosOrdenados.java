package recifecultural.dominio.agenda.acessibilidade;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

/*
 * Padrão Iterator: percorre os recursos de acessibilidade de um evento em ordem
 * de status (CONFIRMADO → REMOVIDO), abstraindo a estrutura interna da coleção
 * e lendo os itens do repositório em páginas.
 */
public class RecursosOrdenados implements Iterable<RecursoAcessibilidade> {

    public static final int TAMANHO_PADRAO_PAGINA = 10;

    private final IRecursoAcessibilidadeRepositorio repositorio;
    private final UUID eventoId;
    private final int tamanhoPagina;

    public RecursosOrdenados(IRecursoAcessibilidadeRepositorio repositorio, UUID eventoId) {
        this(repositorio, eventoId, TAMANHO_PADRAO_PAGINA);
    }

    public RecursosOrdenados(IRecursoAcessibilidadeRepositorio repositorio, UUID eventoId, int tamanhoPagina) {
        if (repositorio == null) throw new IllegalArgumentException("Repositório é obrigatório.");
        if (eventoId == null) throw new IllegalArgumentException("Evento é obrigatório.");
        if (tamanhoPagina <= 0) throw new IllegalArgumentException("Tamanho da página deve ser positivo.");

        this.repositorio = repositorio;
        this.eventoId = eventoId;
        this.tamanhoPagina = tamanhoPagina;
    }

    @Override
    public Iterator<RecursoAcessibilidade> iterator() {
        return new IteradorPorStatus(repositorio, eventoId, tamanhoPagina);
    }

    private static class IteradorPorStatus implements Iterator<RecursoAcessibilidade> {
        private final IRecursoAcessibilidadeRepositorio repositorio;
        private final UUID eventoId;
        private final int tamanhoPagina;
        private List<RecursoAcessibilidade> paginaAtual = List.of();
        private int pagina = 0;
        private int posicaoNaPagina = 0;
        private boolean fim;

        IteradorPorStatus(IRecursoAcessibilidadeRepositorio repositorio, UUID eventoId, int tamanhoPagina) {
            this.repositorio = repositorio;
            this.eventoId = eventoId;
            this.tamanhoPagina = tamanhoPagina;
        }

        @Override
        public boolean hasNext() {
            return carregarPaginaSeNecessario();
        }

        @Override
        public RecursoAcessibilidade next() {
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

            paginaAtual = repositorio.listarRecursosOrdenados(eventoId, pagina, tamanhoPagina);
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
