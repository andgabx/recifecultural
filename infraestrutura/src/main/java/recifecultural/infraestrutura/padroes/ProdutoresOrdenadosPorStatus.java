package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.artista.produtor.Produtor;
import recifecultural.dominio.artista.produtor.StatusProdutor;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/*
 * Padrão Iterator (Par 5 — F5.1 Artistas/Produtores): percorre produtores
 * em ordem de prioridade operacional (ATIVO → SUSPENSO → INATIVO),
 * abstraindo a estrutura interna da coleção.
 */
public class ProdutoresOrdenadosPorStatus implements Iterable<Produtor> {

    private static final Comparator<Produtor> ORDEM_PRIORIDADE = Comparator
            .comparingInt(p -> ordemStatus(p.getStatus()));

    private final List<Produtor> produtores;

    public ProdutoresOrdenadosPorStatus(List<Produtor> produtores) {
        if (produtores == null) throw new IllegalArgumentException("Lista de produtores não pode ser nula.");
        this.produtores = produtores.stream().sorted(ORDEM_PRIORIDADE).toList();
    }

    @Override
    public Iterator<Produtor> iterator() {
        return new IteradorStatus(produtores);
    }

    private static int ordemStatus(StatusProdutor status) {
        return switch (status) {
            case ATIVO -> 0;
            case SUSPENSO -> 1;
            case INATIVO -> 2;
        };
    }

    private static class IteradorStatus implements Iterator<Produtor> {
        private final List<Produtor> lista;
        private int posicao = 0;

        IteradorStatus(List<Produtor> lista) {
            this.lista = lista;
        }

        @Override
        public boolean hasNext() {
            return posicao < lista.size();
        }

        @Override
        public Produtor next() {
            if (!hasNext()) throw new NoSuchElementException();
            return lista.get(posicao++);
        }
    }
}
