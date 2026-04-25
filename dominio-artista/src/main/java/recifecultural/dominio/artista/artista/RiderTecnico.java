package recifecultural.dominio.artista.artista;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public record RiderTecnico(Set<ItemRider> itens) {

    public RiderTecnico {
        if (itens == null) throw new IllegalArgumentException("Itens do rider não podem ser nulos.");
        itens = itens.isEmpty()
                ? Collections.emptySet()
                : Collections.unmodifiableSet(EnumSet.copyOf(itens));
    }

    public static RiderTecnico vazio() { return new RiderTecnico(Set.of()); }
}