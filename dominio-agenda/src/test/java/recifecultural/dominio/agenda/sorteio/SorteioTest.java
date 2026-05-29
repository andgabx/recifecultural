package recifecultural.dominio.agenda.sorteio;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SorteioTest {

    private Sorteio novoSorteio(int vagas) {
        return new Sorteio(
                UUID.randomUUID(),
                UUID.randomUUID(),
                vagas,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                new Random(42));
    }

    @Test
    void vagas_zero_falha() {
        assertThrows(IllegalArgumentException.class, () -> new Sorteio(
                UUID.randomUUID(), UUID.randomUUID(), 0,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2)));
    }

    @Test
    void prazo_apos_apresentacao_falha() {
        assertThrows(IllegalArgumentException.class, () -> new Sorteio(
                UUID.randomUUID(), UUID.randomUUID(), 5,
                LocalDateTime.now().plusDays(10), LocalDateTime.now().plusDays(5)));
    }

    @Test
    void inscrever_duas_vezes_falha() {
        Sorteio s = novoSorteio(2);
        UUID espectador = UUID.randomUUID();
        s.inscrever(espectador);
        assertThrows(IllegalStateException.class, () -> s.inscrever(espectador));
    }

    @Test
    void apurar_com_menos_inscritos_que_vagas_todos_ganhadores() {
        Sorteio s = novoSorteio(5);
        UUID e1 = UUID.randomUUID();
        UUID e2 = UUID.randomUUID();
        s.inscrever(e1);
        s.inscrever(e2);
        s.apurar();
        assertEquals(StatusSorteio.CONCLUIDO, s.getStatus());
        assertEquals(2, s.contarPorStatus(StatusInscricao.GANHADOR));
        assertEquals(0, s.contarPorStatus(StatusInscricao.SUPLENTE));
    }

    @Test
    void apurar_com_mais_inscritos_que_vagas_define_ganhadores_e_suplentes() {
        Sorteio s = novoSorteio(2);
        for (int i = 0; i < 5; i++) s.inscrever(UUID.randomUUID());
        s.apurar();
        assertEquals(2, s.contarPorStatus(StatusInscricao.GANHADOR));
        assertEquals(3, s.contarPorStatus(StatusInscricao.SUPLENTE));
    }

    @Test
    void apurar_retorna_evento_concluido() {
        Sorteio s = novoSorteio(2);
        s.inscrever(UUID.randomUUID());
        s.inscrever(UUID.randomUUID());
        Sorteio.ConcluidoEvento evento = s.apurar();
        assertSame(s, evento.getSorteio());
    }

    @Test
    void desistir_de_ganhador_promove_suplente_e_retorna_evento() {
        Sorteio s = novoSorteio(1);
        UUID e1 = UUID.randomUUID();
        UUID e2 = UUID.randomUUID();
        s.inscrever(e1);
        s.inscrever(e2);
        s.apurar();
        UUID ganhador = s.getInscricoes().stream()
                .filter(i -> i.getStatus() == StatusInscricao.GANHADOR)
                .findFirst().get().getEspectadorId();
        Optional<Sorteio.SuplentePromovidoEvento> evento = s.desistir(ganhador);
        assertEquals(1, s.contarPorStatus(StatusInscricao.GANHADOR));
        assertEquals(1, s.contarPorStatus(StatusInscricao.DESISTENTE));
        assertTrue(evento.isPresent());
        assertEquals(ganhador, evento.get().getEspectadorDesistenteId());
    }

    @Test
    void cancelar_retorna_evento_e_marca_inscricoes() {
        Sorteio s = novoSorteio(2);
        s.inscrever(UUID.randomUUID());
        Sorteio.CanceladoEvento evento = s.cancelar();
        assertEquals(StatusSorteio.CANCELADO, s.getStatus());
        assertSame(s, evento.getSorteio());
    }
}
