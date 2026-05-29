package recifecultural.infraestrutura.evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoObservador;

/*
 * Padrão Observer: implementa o EventoBarramento usando o mecanismo de eventos
 * do Spring como infraestrutura de despacho. Desacopla publicadores de
 * assinantes sem conhecimento mútuo.
 */
@Component
public class EventoBarramentoImpl implements EventoBarramento {

    @Autowired
    private ApplicationEventMulticaster multicaster;

    @Autowired
    private ApplicationEventPublisher publicador;

    @Override
    public <E> void adicionar(EventoObservador<E> observador) {
        multicaster.addApplicationListener(new SmartApplicationListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void onApplicationEvent(org.springframework.context.ApplicationEvent evento) {
                if (evento instanceof org.springframework.context.PayloadApplicationEvent<?> payloadEvento) {
                    observador.observarEvento((E) payloadEvento.getPayload());
                }
            }

            @Override
            public boolean supportsEventType(Class<? extends org.springframework.context.ApplicationEvent> tipo) {
                return org.springframework.context.PayloadApplicationEvent.class.isAssignableFrom(tipo);
            }

            @Override
            public boolean supportsSourceType(Class<?> tipoFonte) {
                return true;
            }
        });
    }

    @Override
    public <E> void postar(E evento) {
        publicador.publishEvent(evento);
    }
}
