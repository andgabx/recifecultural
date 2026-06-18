package recifecultural.dominio.patrocinio;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PatrocinioTest {

    private Patrocinio novoPatrocinioAtivo(LocalDateTime dataEvento, ModalidadeContribuicao modalidade) {
        Patrocinio p = new Patrocinio(
                PatrocinioId.novo(),
                new EventoId(UUID.randomUUID()),
                "Patrocinador X",
                "BEBIDAS",
                TipoPatrocinio.MASTER,
                modalidade,
                new BigDecimal("1000.00"),
                dataEvento);
        p.ativar();
        return p;
    }

    private Patrocinio novoPatrocinioAtivoComValor(LocalDateTime dataEvento, ModalidadeContribuicao modalidade, BigDecimal valor) {
        Patrocinio p = new Patrocinio(
                PatrocinioId.novo(),
                new EventoId(UUID.randomUUID()),
                "Patrocinador X",
                "BEBIDAS",
                TipoPatrocinio.MASTER,
                modalidade,
                valor,
                dataEvento);
        p.ativar();
        return p;
    }

    @Test
    void nasce_em_proposta() {
        Patrocinio p = new Patrocinio(
                PatrocinioId.novo(), new EventoId(UUID.randomUUID()),
                "Y", "ALIMENTACAO", TipoPatrocinio.ASSOCIADO,
                ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL, new BigDecimal("500"),
                LocalDateTime.now().plusDays(10));
        assertEquals(StatusPatrocinio.PROPOSTA, p.getStatus());
    }

    @Test
    void valor_zero_falha() {
        assertThrows(Exception.class, () -> new Patrocinio(
                PatrocinioId.novo(), new EventoId(UUID.randomUUID()),
                "X", "Y", TipoPatrocinio.MASTER,
                ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL, BigDecimal.ZERO,
                LocalDateTime.now().plusDays(5)));
    }


    @Test
    void ativar_transicao_proposta_para_ativo() {
        Patrocinio p = new Patrocinio(
                PatrocinioId.novo(), new EventoId(UUID.randomUUID()),
                "Z", "TECNOLOGIA", TipoPatrocinio.ASSOCIADO,
                ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL, new BigDecimal("200.00"),
                LocalDateTime.now().plusDays(15));
        assertEquals(StatusPatrocinio.PROPOSTA, p.getStatus());
        p.ativar();
        assertEquals(StatusPatrocinio.ATIVO, p.getStatus());
    }

    @Test
    void ativar_ja_ativo_lanca_excecao() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(10), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        assertThrows(Exception.class, p::ativar);
    }

    @Test
    void encerrar_transicao_ativo_para_encerrado() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(10), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        p.encerrar();
        assertEquals(StatusPatrocinio.ENCERRADO, p.getStatus());
    }

    @Test
    void encerrar_nao_ativo_lanca_excecao() {
        Patrocinio p = new Patrocinio(
                PatrocinioId.novo(), new EventoId(UUID.randomUUID()),
                "W", "MUSICA", TipoPatrocinio.MASTER,
                ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL, new BigDecimal("300.00"),
                LocalDateTime.now().plusDays(10));
        assertThrows(Exception.class, p::encerrar);
    }


    @Test
    void cancelar_pelo_evento_acima_de_sete_dias_reembolso_total() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(10), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorEvento(LocalDateTime.now());
        assertEquals(0, new BigDecimal("1000.00").compareTo(r.getReembolso()));
        assertEquals(0, BigDecimal.ZERO.compareTo(r.getMulta()));
        assertEquals(StatusPatrocinio.CANCELADO_EVENTO, p.getStatus());
    }

    @Test
    void cancelar_pelo_evento_entre_dois_e_sete_dias_reembolsa_metade() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(5), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorEvento(LocalDateTime.now());
        assertEquals(0, new BigDecimal("500.00").compareTo(r.getReembolso()));
    }

    @Test
    void cancelar_pelo_evento_abaixo_de_dois_dias_sem_reembolso() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(1), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorEvento(LocalDateTime.now());
        assertEquals(0, BigDecimal.ZERO.compareTo(r.getReembolso()));
    }


    @Test
    void cancelar_pelo_evento_exatamente_sete_dias_reembolsa_metade() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(7), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorEvento(LocalDateTime.now());
        assertEquals(0, new BigDecimal("500.00").compareTo(r.getReembolso()));
        assertEquals(StatusPatrocinio.CANCELADO_EVENTO, p.getStatus());
    }

    @Test
    void cancelar_pelo_evento_exatamente_dois_dias_reembolsa_metade() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(2), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorEvento(LocalDateTime.now());
        assertEquals(0, new BigDecimal("500.00").compareTo(r.getReembolso()));
        assertEquals(StatusPatrocinio.CANCELADO_EVENTO, p.getStatus());
    }


    @Test
    void cancelar_pelo_patrocinador_acima_de_quinze_dias_sem_multa() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(20), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorPatrocinador(LocalDateTime.now());
        assertEquals(0, new BigDecimal("1000.00").compareTo(r.getReembolso()));
        assertEquals(0, BigDecimal.ZERO.compareTo(r.getMulta()));
    }

    @Test
    void cancelar_pelo_patrocinador_dentro_de_quinze_dias_aplica_multa_de_vinte_porcento() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(10), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorPatrocinador(LocalDateTime.now());
        assertEquals(0, new BigDecimal("800.00").compareTo(r.getReembolso()));
        assertEquals(0, new BigDecimal("200.00").compareTo(r.getMulta()));
    }

    @Test
    void cancelar_pelo_patrocinador_exatamente_quinze_dias_aplica_multa() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(15), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento r = p.cancelarPorPatrocinador(LocalDateTime.now());
        assertEquals(0, new BigDecimal("800.00").compareTo(r.getReembolso()));
        assertEquals(0, new BigDecimal("200.00").compareTo(r.getMulta()));
        assertEquals(StatusPatrocinio.CANCELADO_PATROCINADOR, p.getStatus());
    }


    @Test
    void subsidio_aplica_piso_minimo_de_um_real() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(10), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        ResultadoSubsidio r = p.calcularSubsidio(new BigDecimal("5.00"));
        assertEquals(0, new BigDecimal("1.00").compareTo(r.getNovoPrecoSocial()));
        assertTrue(r.isPisoAplicado());
    }

    @Test
    void subsidio_so_para_modalidade_subsidio() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(10), ModalidadeContribuicao.FINANCEIRO);
        assertThrows(Exception.class, () -> p.calcularSubsidio(new BigDecimal("10.00")));
    }

    @Test
    void cancelar_patrocinio_retorna_evento_com_resultado() {
        Patrocinio p = novoPatrocinioAtivo(LocalDateTime.now().plusDays(10), ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL);
        Patrocinio.CanceladoEvento evento = p.cancelarPorEvento(LocalDateTime.now());
        assertNotNull(evento);
        assertSame(p, evento.getPatrocinio());
        assertNotNull(evento.getMotivo());
    }

    @Test
    void subsidio_nao_pode_exceder_preco_menos_piso() {
        Patrocinio p = novoPatrocinioAtivoComValor(
                LocalDateTime.now().plusDays(10),
                ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL,
                new BigDecimal("29.50"));
        ResultadoSubsidio r = p.calcularSubsidio(new BigDecimal("30.00"));
        assertEquals(0, new BigDecimal("1.00").compareTo(r.getNovoPrecoSocial()));
        assertTrue(r.isPisoAplicado());
    }
}
