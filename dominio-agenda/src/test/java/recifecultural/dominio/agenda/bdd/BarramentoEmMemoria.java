package recifecultural.dominio.agenda.bdd;

import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoObservador;

import java.util.ArrayList;
import java.util.List;

/*
 * Implementação em memória do EventoBarramento para uso em testes BDD.
 * Dispara eventos sincronamente para observadores registrados, permitindo
 * verificar o fluxo Observer (publicar → reagir → notificar) sem Spring.
 */
public class BarramentoEmMemoria implements EventoBarramento {

    private final List<EventoObservador<?>> observadores = new ArrayList<>();

    @Override
    public <E> void adicionar(EventoObservador<E> observador) {
        observadores.add(observador);
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <E> void postar(E evento) {
        for (EventoObservador observador : observadores) {
            try {
                observador.observarEvento(evento);
            } catch (ClassCastException ignored) {
                // Observador não está interessado neste tipo de evento.
            }
        }
    }
}
