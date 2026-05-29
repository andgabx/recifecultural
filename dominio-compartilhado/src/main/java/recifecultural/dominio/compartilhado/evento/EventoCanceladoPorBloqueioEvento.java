package recifecultural.dominio.compartilhado.evento;

import java.util.UUID;

/*
 * Evento de domínio publicado no barramento quando um bloqueio administrativo
 * causa o cancelamento automático de eventos do calendário. Permite que outros
 * Bounded Contexts reajam (ex.: F3.2 Notificações) sem acoplamento direto.
 */
public record EventoCanceladoPorBloqueioEvento(
        UUID eventoId,
        UUID promotorId,
        String tituloEvento,
        String justificativaBloqueio) {
}
