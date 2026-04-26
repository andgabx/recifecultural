package recifecultural.dominio.agenda.comentario;

public final class Nota {

    private static final int MINIMO = 1;
    private static final int MAXIMO = 5;

    private final int valor;

    public Nota(int valor) {
        if (valor < MINIMO || valor > MAXIMO)
            throw new IllegalArgumentException("Nota deve estar entre " + MINIMO + " e " + MAXIMO + ".");
        this.valor = valor;
    }

    public int getValor() {
        return valor;
    }
}
