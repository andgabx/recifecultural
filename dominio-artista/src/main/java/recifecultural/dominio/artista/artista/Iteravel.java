package recifecultural.dominio.artista.artista;

public interface Iteravel<T> {
    Iterador<T> criarIterador();
}
