package recifecultural.dominio.artista.artista;

import java.util.NoSuchElementException;
import java.util.Set;

public class IteradorDeItensRider implements Iterador<ItemRider> {

    private final Object[] elementos;
    private int posicaoAtual;

    public IteradorDeItensRider(Set<ItemRider> itens) {
        if (itens == null) throw new IllegalArgumentException("O conjunto de itens do rider não pode ser nulo.");
        this.elementos = itens.toArray();
        this.posicaoAtual = 0;
    }

    @Override
    public boolean temProximo() {
        return posicaoAtual < elementos.length;
    }

    @Override
    public ItemRider proximo() {
        if (!temProximo()) {
            throw new NoSuchElementException("Não há mais itens no rider técnico para iterar.");
        }
        return (ItemRider) elementos[posicaoAtual++];
    }
}
