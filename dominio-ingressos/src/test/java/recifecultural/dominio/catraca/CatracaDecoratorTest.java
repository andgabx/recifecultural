package recifecultural.dominio.catraca;

import org.junit.jupiter.api.Test;
import recifecultural.dominio.catraca.validacoes.ValidadorAcesso;
import recifecultural.dominio.catraca.validacoes.ValidadorAcessoBase;
import recifecultural.dominio.catraca.validacoes.ValidarDuplaEntradaDecorator;
import recifecultural.dominio.catraca.validacoes.ValidarEstornoDecorator;
import recifecultural.dominio.catraca.validacoes.ValidarPortaoDecorator;
import recifecultural.dominio.catraca.validacoes.ValidarToleranciaAtrasoDecorator;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CatracaDecoratorTest {

    private final ValidadorAcesso base = new ValidadorAcessoBase();

    private IngressoCatraca ingressoValido(TipoIngresso tipo) {
        return new IngressoCatraca(
                new IngressoCatracaId("ING-001"),
                "EVT-001",
                StatusIngressoCatraca.VALIDO,
                LocalDateTime.now(),
                tipo,
                null);
    }

    private IngressoCatraca ingressoComPortao(String portao) {
        return new IngressoCatraca(
                new IngressoCatracaId("ING-002"),
                "EVT-001",
                StatusIngressoCatraca.VALIDO,
                LocalDateTime.now(),
                TipoIngresso.COMUM,
                portao);
    }

    private IngressoCatraca ingressoComStatus(StatusIngressoCatraca status) {
        return new IngressoCatraca(
                new IngressoCatracaId("ING-003"),
                "EVT-001",
                status,
                LocalDateTime.now(),
                TipoIngresso.COMUM,
                null);
    }

    // ===== ValidarEstornoDecorator =====

    @Test
    void estorno_cancelado_ou_reembolsado_lanca_excecao() {
        ValidarEstornoDecorator decorator = new ValidarEstornoDecorator(base);
        IngressoCatraca ingresso = ingressoComStatus(StatusIngressoCatraca.CANCELADO_OU_REEMBOLSADO);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));

        assertEquals("Entrada Negada: Este ingresso consta como cancelado ou reembolsado.", ex.getMessage());
    }

    @Test
    void estorno_ingresso_valido_passa_sem_excecao() {
        ValidarEstornoDecorator decorator = new ValidarEstornoDecorator(base);
        IngressoCatraca ingresso = ingressoComStatus(StatusIngressoCatraca.VALIDO);

        assertDoesNotThrow(() -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));
    }

    // ===== ValidarDuplaEntradaDecorator =====

    @Test
    void dupla_entrada_ingresso_utilizado_lanca_excecao() {
        ValidarDuplaEntradaDecorator decorator = new ValidarDuplaEntradaDecorator(base);
        IngressoCatraca ingresso = ingressoComStatus(StatusIngressoCatraca.UTILIZADO);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));

        assertEquals("ALERTA FRAUDE: Este ingresso já foi utilizado.", ex.getMessage());
    }

    @Test
    void dupla_entrada_ingresso_valido_passa_sem_excecao() {
        ValidarDuplaEntradaDecorator decorator = new ValidarDuplaEntradaDecorator(base);
        IngressoCatraca ingresso = ingressoComStatus(StatusIngressoCatraca.VALIDO);

        assertDoesNotThrow(() -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));
    }

    // ===== ValidarPortaoDecorator =====

    @Test
    void portao_errado_lanca_excecao() {
        ValidarPortaoDecorator decorator = new ValidarPortaoDecorator(base);
        IngressoCatraca ingresso = ingressoComPortao("PORTAO-B");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));

        assertTrue(ex.getMessage().contains("PORTAO-B"));
    }

    @Test
    void portao_correto_passa_sem_excecao() {
        ValidarPortaoDecorator decorator = new ValidarPortaoDecorator(base);
        IngressoCatraca ingresso = ingressoComPortao("PORTAO-A");

        assertDoesNotThrow(() -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));
    }

    @Test
    void portao_null_passa_sem_checar() {
        ValidarPortaoDecorator decorator = new ValidarPortaoDecorator(base);
        IngressoCatraca ingresso = ingressoComPortao(null);

        assertDoesNotThrow(() -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-QUALQUER"));
    }

    // ===== ValidarToleranciaAtrasoDecorator =====

    @Test
    void tolerancia_comum_acima_de_15min_lanca_excecao() {
        ValidarToleranciaAtrasoDecorator decorator = new ValidarToleranciaAtrasoDecorator(base);
        LocalDateTime inicioEvento = LocalDateTime.now().minusMinutes(20);

        IngressoCatraca ingresso = new IngressoCatraca(
                new IngressoCatracaId("ING-T01"),
                "EVT-001",
                StatusIngressoCatraca.VALIDO,
                inicioEvento,
                TipoIngresso.COMUM,
                null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));

        assertEquals("Entrada Negada: O limite de 15 minutos de atraso foi excedido. As portas do teatro estão fechadas.", ex.getMessage());
    }

    @Test
    void tolerancia_vip_sem_limite_passa_sem_excecao() {
        ValidarToleranciaAtrasoDecorator decorator = new ValidarToleranciaAtrasoDecorator(base);
        LocalDateTime inicioEvento = LocalDateTime.now().minusMinutes(60);

        IngressoCatraca ingresso = new IngressoCatraca(
                new IngressoCatracaId("ING-T02"),
                "EVT-001",
                StatusIngressoCatraca.VALIDO,
                inicioEvento,
                TipoIngresso.VIP,
                null);

        assertDoesNotThrow(() -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-VIP"));
    }

    @Test
    void tolerancia_comum_dentro_do_prazo_passa_sem_excecao() {
        ValidarToleranciaAtrasoDecorator decorator = new ValidarToleranciaAtrasoDecorator(base);
        LocalDateTime inicioEvento = LocalDateTime.now().minusMinutes(10);

        IngressoCatraca ingresso = new IngressoCatraca(
                new IngressoCatracaId("ING-T03"),
                "EVT-001",
                StatusIngressoCatraca.VALIDO,
                inicioEvento,
                TipoIngresso.COMUM,
                null);

        assertDoesNotThrow(() -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));
    }

    @Test
    void tolerancia_meia_entrada_sem_limite_de_atraso_passa_sem_excecao() {
        ValidarToleranciaAtrasoDecorator decorator = new ValidarToleranciaAtrasoDecorator(base);
        LocalDateTime inicioEvento = LocalDateTime.now().minusMinutes(60);

        IngressoCatraca ingresso = new IngressoCatraca(
                new IngressoCatracaId("ING-T04"),
                "EVT-001",
                StatusIngressoCatraca.VALIDO,
                inicioEvento,
                TipoIngresso.MEIA_ENTRADA,
                null);

        // MEIA_ENTRADA is not COMUM, so the 15-min restriction does not apply
        assertDoesNotThrow(() -> decorator.validar(ingresso, LocalDateTime.now(), "PORTAO-A"));
    }
}
