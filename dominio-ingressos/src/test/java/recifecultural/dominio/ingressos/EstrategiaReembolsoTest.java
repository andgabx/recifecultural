package recifecultural.dominio.ingressos;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EstrategiaReembolsoTest {

    private final SeletorEstrategiaReembolso seletor = new SeletorEstrategiaReembolso();

    @Test
    void pix_retorna_estrategia_imediata() {
        EstrategiaProcessamentoReembolso estrategia = seletor.selecionar(MetodoPagamento.PIX);
        assertInstanceOf(EstrategiaReembolsoImediato.class, estrategia);
    }

    @Test
    void cartao_credito_retorna_estrategia_bancaria() {
        EstrategiaProcessamentoReembolso estrategia = seletor.selecionar(MetodoPagamento.CARTAO_CREDITO);
        assertInstanceOf(EstrategiaReembolsoBancario.class, estrategia);
    }

    @Test
    void estrategia_imediata_prazo_processamento() {
        EstrategiaReembolsoImediato estrategia = new EstrategiaReembolsoImediato();
        assertEquals("Imediato", estrategia.prazoProcessamento());
    }

    @Test
    void estrategia_imediata_descricao_nao_nula() {
        EstrategiaReembolsoImediato estrategia = new EstrategiaReembolsoImediato();
        assertNotNull(estrategia.descricao());
        assertFalse(estrategia.descricao().isBlank());
    }

    @Test
    void estrategia_imediata_aplicavel_a_pix() {
        EstrategiaReembolsoImediato estrategia = new EstrategiaReembolsoImediato();
        assertTrue(estrategia.aplicavelA(MetodoPagamento.PIX));
    }

    @Test
    void estrategia_imediata_nao_aplicavel_a_cartao() {
        EstrategiaReembolsoImediato estrategia = new EstrategiaReembolsoImediato();
        assertFalse(estrategia.aplicavelA(MetodoPagamento.CARTAO_CREDITO));
    }

    @Test
    void estrategia_bancaria_prazo_processamento() {
        EstrategiaReembolsoBancario estrategia = new EstrategiaReembolsoBancario();
        assertEquals("Até 2 dias úteis", estrategia.prazoProcessamento());
    }

    @Test
    void estrategia_bancaria_aplicavel_a_cartao_credito() {
        EstrategiaReembolsoBancario estrategia = new EstrategiaReembolsoBancario();
        assertTrue(estrategia.aplicavelA(MetodoPagamento.CARTAO_CREDITO));
    }

    @Test
    void estrategia_bancaria_nao_aplicavel_a_pix() {
        EstrategiaReembolsoBancario estrategia = new EstrategiaReembolsoBancario();
        assertFalse(estrategia.aplicavelA(MetodoPagamento.PIX));
    }
}
