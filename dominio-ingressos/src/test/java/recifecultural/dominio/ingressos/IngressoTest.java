package recifecultural.dominio.ingressos;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class IngressoTest {

    private Ingresso novoIngressoAtivo(LocalDateTime apresentacao) {
        return new Ingresso(
                IngressoId.novo(),
                UUID.randomUUID(),
                apresentacao,
                TipoIngresso.INTEIRA,
                new BigDecimal("100.00"),
                "QR-1",
                "TX-1",
                MetodoPagamento.PIX);
    }

    @Test
    void evento_de_compra_disponivel_apos_construcao() {
        Ingresso ingresso = novoIngressoAtivo(LocalDateTime.now().plusDays(10));
        Ingresso.CompradoEvento evento = ingresso.eventoCompra();
        assertNotNull(evento);
        assertSame(ingresso, evento.getIngresso());
    }

    @Test
    void reembolso_total_acima_de_sete_dias() {
        Ingresso ingresso = novoIngressoAtivo(LocalDateTime.now().plusDays(10));
        BigDecimal valor = ingresso.calcularReembolso(LocalDateTime.now());
        assertEquals(0, new BigDecimal("100.00").compareTo(valor));
    }

    @Test
    void reembolso_metade_entre_dois_e_sete_dias() {
        Ingresso ingresso = novoIngressoAtivo(LocalDateTime.now().plusDays(5));
        BigDecimal valor = ingresso.calcularReembolso(LocalDateTime.now());
        assertEquals(0, new BigDecimal("50.00").compareTo(valor));
    }

    @Test
    void reembolso_zero_abaixo_de_dois_dias() {
        Ingresso ingresso = novoIngressoAtivo(LocalDateTime.now().plusDays(1));
        BigDecimal valor = ingresso.calcularReembolso(LocalDateTime.now());
        assertEquals(0, BigDecimal.ZERO.compareTo(valor));
    }

    @Test
    void reembolsar_ingresso_ja_reembolsado_falha() {
        Ingresso ingresso = novoIngressoAtivo(LocalDateTime.now().plusDays(10));
        ingresso.reembolsar(new BigDecimal("100.00"));
        assertThrows(Exception.class, () -> ingresso.reembolsar(new BigDecimal("100.00")));
    }

    @Test
    void reembolso_retorna_evento() {
        Ingresso ingresso = novoIngressoAtivo(LocalDateTime.now().plusDays(10));
        Ingresso.ReembolsadoEvento evento = ingresso.reembolsar(new BigDecimal("100.00"));
        assertNotNull(evento);
        assertSame(ingresso, evento.getIngresso());
    }

    @Test
    void marcar_utilizado_apenas_se_ativo() {
        Ingresso ingresso = novoIngressoAtivo(LocalDateTime.now().plusDays(10));
        ingresso.marcarComoUtilizado();
        assertEquals(StatusIngresso.UTILIZADO, ingresso.getStatus());
        assertThrows(Exception.class, ingresso::marcarComoUtilizado);
    }

    @Test
    void valor_pago_negativo_falha() {
        assertThrows(Exception.class, () -> new Ingresso(
                IngressoId.novo(),
                UUID.randomUUID(),
                LocalDateTime.now().plusDays(5),
                TipoIngresso.INTEIRA,
                new BigDecimal("-1"),
                "QR", "TX", MetodoPagamento.PIX));
    }
}
