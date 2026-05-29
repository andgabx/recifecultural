package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.agenda.sorteio.Inscricao;
import recifecultural.dominio.agenda.sorteio.StatusInscricao;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/*
 * Padrão Iterator: percorre inscrições de um sorteio em ordem de prioridade
 * (GANHADOR → SUPLENTE → INSCRITO → outros), abstraindo a estrutura interna
 * da coleção e garantindo encapsulamento.
 */
public class InscricoesOrdenadas implements Iterable<Inscricao> {

    private static final Comparator<Inscricao> ORDEM_PRIORIDADE = Comparator
            .comparingInt(i -> ordemStatus(i.getStatus()));

    private final List<Inscricao> inscricoes;

    public InscricoesOrdenadas(List<Inscricao> inscricoes) {
        if (inscricoes == null) throw new IllegalArgumentException("Lista de inscrições não pode ser nula.");
        this.inscricoes = inscricoes.stream()
                .sorted(ORDEM_PRIORIDADE)
                .toList();
    }

    @Override
    public Iterator<Inscricao> iterator() {
        return new IteradorPrioridade(inscricoes);
    }

    private static int ordemStatus(StatusInscricao status) {
        return switch (status) {
            case GANHADOR -> 0;
            case SUPLENTE -> 1;
            case INSCRITO -> 2;
            case DESISTENTE -> 3;
            case CANCELADA -> 4;
        };
    }

    private static class IteradorPrioridade implements Iterator<Inscricao> {
        private final List<Inscricao> lista;
        private int posicao = 0;

        IteradorPrioridade(List<Inscricao> lista) {
            this.lista = lista;
        }

        @Override
        public boolean hasNext() {
            return posicao < lista.size();
        }

        @Override
        public Inscricao next() {
            if (!hasNext()) throw new NoSuchElementException();
            return lista.get(posicao++);
        }
    }
}
