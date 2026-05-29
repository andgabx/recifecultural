package recifecultural.dominio.agenda.bloqueioadministrativo;

import org.junit.jupiter.api.Test;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BloqueioAdministrativoTest {

    @Test
    void cria_bloqueio_e_evento_de_criacao_disponivel() {
        BloqueioAdministrativo b = new BloqueioAdministrativo(
                EspacoId.novo(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "Manutenção programada do telhado.");
        assertTrue(b.isAtivo());
        BloqueioAdministrativo.CriadoEvento evento = b.eventoCriacao();
        assertNotNull(evento);
        assertSame(b, evento.getBloqueio());
    }

    @Test
    void justificativa_em_branco_falha() {
        assertThrows(IllegalArgumentException.class, () -> new BloqueioAdministrativo(
                EspacoId.novo(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                ""));
    }

    @Test
    void justificativa_curta_falha() {
        assertThrows(IllegalArgumentException.class, () -> new BloqueioAdministrativo(
                EspacoId.novo(),
                LocalDate.now(),
                LocalDate.now().plusDays(5),
                "curta"));
    }

    @Test
    void data_fim_anterior_a_inicio_falha() {
        assertThrows(IllegalArgumentException.class, () -> new BloqueioAdministrativo(
                EspacoId.novo(),
                LocalDate.now().plusDays(5),
                LocalDate.now(),
                "Justificativa válida e suficientemente longa."));
    }

    @Test
    void desativar_inverte_estado() {
        BloqueioAdministrativo b = new BloqueioAdministrativo(
                EspacoId.novo(), LocalDate.now(), LocalDate.now().plusDays(2),
                "Justificativa válida e suficientemente longa.");
        b.desativar();
        assertFalse(b.isAtivo());
    }
}
