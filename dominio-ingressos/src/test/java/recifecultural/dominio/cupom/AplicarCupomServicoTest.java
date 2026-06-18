package recifecultural.dominio.cupom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AplicarCupomServicoTest {

    private ICupomRepositorio repositorio;
    private AplicarCupomServico servico;

    @BeforeEach
    void setUp() {
        repositorio = mock(ICupomRepositorio.class);
        servico = new AplicarCupomServico(repositorio);
    }

    private Cupom cupomPercentual(int percentual) {
        return new Cupom(
                new CupomId("ID-1"), "PROMO20", TipoDesconto.PERCENTUAL,
                new BigDecimal(percentual), new BigDecimal("100.00"),
                10, 1,
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(10),
                "TEATRO");
    }

    @Test
    void preview_desconto_calcula_sem_persistir() {
        Cupom cupom = cupomPercentual(20);
        when(repositorio.buscarPorCodigo("PROMO20")).thenReturn(cupom);

        servico.previewDesconto("PROMO20", "CPF-123", new BigDecimal("200.00"), "TEATRO");

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void preview_desconto_retorna_valores_corretos() {
        Cupom cupom = cupomPercentual(20);
        when(repositorio.buscarPorCodigo("PROMO20")).thenReturn(cupom);

        AplicarCupomServico.PreviewDesconto preview =
                servico.previewDesconto("PROMO20", "CPF-123", new BigDecimal("200.00"), "TEATRO");

        assertNotNull(preview);
        assertEquals("PERCENTUAL", preview.tipoDesconto());
        assertEquals(0, new BigDecimal("20").compareTo(preview.configuracaoDesconto()));
        assertEquals(0, new BigDecimal("40.00").compareTo(preview.descontoCalculado()));
        assertEquals(0, new BigDecimal("160.00").compareTo(preview.valorFinal()));
    }

    @Test
    void preview_desconto_cupom_inexistente_lanca_excecao() {
        when(repositorio.buscarPorCodigo("INVALIDO")).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servico.previewDesconto("INVALIDO", "CPF-123", new BigDecimal("200.00"), "TEATRO"));

        assertEquals("Cupom não encontrado: INVALIDO", ex.getMessage());
    }

    @Test
    void preview_desconto_cupom_expirado_lanca_excecao() {
        Cupom cupomExpirado = new Cupom(
                new CupomId("ID-EXP"), "PROMO20", TipoDesconto.PERCENTUAL,
                new BigDecimal("20"), new BigDecimal("100.00"),
                10, 1,
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now().minusDays(1),
                "TEATRO");
        when(repositorio.buscarPorCodigo("PROMO20")).thenReturn(cupomExpirado);

        assertThrows(IllegalArgumentException.class,
                () -> servico.previewDesconto("PROMO20", "CPF-123", new BigDecimal("200.00"), "TEATRO"));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void preview_desconto_categoria_errada_lanca_excecao() {
        Cupom cupom = cupomPercentual(20);
        when(repositorio.buscarPorCodigo("PROMO20")).thenReturn(cupom);

        assertThrows(IllegalArgumentException.class,
                () -> servico.previewDesconto("PROMO20", "CPF-123", new BigDecimal("200.00"), "SHOW"));

        verify(repositorio, never()).salvar(any());
    }

    @Test
    void preview_desconto_nao_registra_uso_do_cupom() {
        Cupom cupom = cupomPercentual(20);
        when(repositorio.buscarPorCodigo("PROMO20")).thenReturn(cupom);

        servico.previewDesconto("PROMO20", "CPF-123", new BigDecimal("200.00"), "TEATRO");

        assertEquals(0, cupom.getUsosGlobais());
        assertFalse(cupom.getCpfsQueJaUsaram().contains("CPF-123"));
    }
}
