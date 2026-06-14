package recifecultural.dominio.agenda.comum;

/*
 * Padrão Template Method: define o esqueleto de uma operação de domínio que
 * altera um agregado — buscar → aplicar regra → persistir → notificar — deixando
 * cada passo específico a cargo das subclasses. A ordem dos passos é fixa
 * (executar() é final e não pode ser sobrescrito).
 */
public abstract class OperacaoDominioTemplate<T> {

    public final void executar() {
        T agregado = buscar();
        aplicarRegra(agregado);
        persistir(agregado);
        notificar(agregado);
    }

    /** Carrega o agregado alvo da operação. */
    protected abstract T buscar();

    /** Aplica a regra de negócio sobre o agregado (passo que varia em cada operação). */
    protected abstract void aplicarRegra(T agregado);

    /** Persiste o agregado alterado. */
    protected abstract void persistir(T agregado);

    /** Hook opcional: por padrão não notifica ninguém. */
    protected void notificar(T agregado) {
    }
}
