package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.espaco.setor.Assento;
import recifecultural.dominio.espaco.setor.StatusAssento;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/*
 * Padrão Iterator (Par 5 — F5.2 Setores/Suporte): percorre assentos
 * em ordem de disponibilidade operacional (LIVRE → PRE_RESERVADO →
 * OCUPADO → BLOQUEADO), abstraindo a estrutura interna do setor.
 */
public class AssentosOrdenadosPorStatus implements Iterable<Assento> {

    private static final Comparator<Assento> ORDEM_DISPONIBILIDADE = Comparator
            .comparingInt(a -> ordemStatus(a.getStatus()));

    private final List<Assento> assentos;

    public AssentosOrdenadosPorStatus(List<Assento> assentos) {
        if (assentos == null) throw new IllegalArgumentException("Lista de assentos não pode ser nula.");
        this.assentos = assentos.stream().sorted(ORDEM_DISPONIBILIDADE).toList();
    }

    @Override
    public Iterator<Assento> iterator() {
        return new IteradorDisponibilidade(assentos);
    }

    private static int ordemStatus(StatusAssento status) {
        return switch (status) {
            case LIVRE -> 0;
            case PRE_RESERVADO -> 1;
            case OCUPADO -> 2;
            case BLOQUEADO -> 3;
        };
    }

    private static class IteradorDisponibilidade implements Iterator<Assento> {
        private final List<Assento> lista;
        private int posicao = 0;

        IteradorDisponibilidade(List<Assento> lista) {
            this.lista = lista;
        }

        @Override
        public boolean hasNext() {
            return posicao < lista.size();
        }

        @Override
        public Assento next() {
            if (!hasNext()) throw new NoSuchElementException();
            return lista.get(posicao++);
        }
    }
}
