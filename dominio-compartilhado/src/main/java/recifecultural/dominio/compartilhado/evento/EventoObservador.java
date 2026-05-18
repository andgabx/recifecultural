package recifecultural.dominio.compartilhado.evento;

public interface EventoObservador<E> {
    void observarEvento(E evento);
}
