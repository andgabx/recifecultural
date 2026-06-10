package recifecultural.dominio.artista.artista;

public interface Iterador<T> {
    boolean temProximo();
    T proximo();
}