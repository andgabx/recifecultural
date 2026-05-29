package recifecultural.dominio.catraca;

import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.mockito.Mockito;
import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.compartilhado.evento.EventoObservador;
import recifecultural.dominio.ingressos.*;
import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PassosCatraca implements EventoBarramento {

    private ICatracaRepositorio repositorioMock = mock(ICatracaRepositorio.class);
    private CatracaServico servico = new CatracaServico(repositorioMock);
    private IngressoCatraca ingressoNoBanco;
    private LocalDateTime horarioEvento;
    private Exception excecaoCapturada;
    private String resultadoAcesso;

    @Override
    public <E> void adicionar(EventoObservador<E> observador) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <E> void postar(E evento) {
        if (evento instanceof Ingresso.ReembolsadoEvento reembolsado) {
            servico.inativarIngresso(reembolsado.getIngresso().getCodigoQr());
        }
    }

    @Dado("que o evento {string} começa às {string}")
    public void que_o_evento_comeca_as(String nome, String horario) {
        horarioEvento = LocalDateTime.of(LocalDate.now(), LocalTime.parse(horario));
    }
    @Dado("que o espectador possui o ingresso {string} com status {string}")
    @Dado("que o ingresso {string} já possui o status {string}")
    public void setup_ingresso_base(String id, String status) {
        ingressoNoBanco = new IngressoCatraca(
                new IngressoCatracaId(id), "EVT-1",
                StatusIngressoCatraca.valueOf(status),
                horarioEvento, TipoIngresso.COMUM, "Portão A"
        );
        when(repositorioMock.buscarPorId(id)).thenReturn(ingressoNoBanco);
    }

    @Dado("o ingresso é do tipo {string} e pertence ao {string}")
    public void setup_detalhes_ingresso(String tipo, String portao) {
        ingressoNoBanco = new IngressoCatraca(
                ingressoNoBanco.getId(), "EVT-1",
                ingressoNoBanco.getStatus(),
                horarioEvento,
                TipoIngresso.valueOf(tipo),
                portao
        );
        when(repositorioMock.buscarPorId(ingressoNoBanco.getId().getValor())).thenReturn(ingressoNoBanco);
    }

    @Dado("que o ingresso {string} é válido, mas o cliente solicitou reembolso agora")
    public void integracao_reembolso_catraca(String codigoQr) {
        horarioEvento = LocalDateTime.of(LocalDate.now(), LocalTime.parse("20:00"));
        ingressoNoBanco = new IngressoCatraca(
                new IngressoCatracaId(codigoQr), "EVT-1",
                StatusIngressoCatraca.VALIDO,
                horarioEvento, TipoIngresso.COMUM, "Portão A"
        );
        when(repositorioMock.buscarPorId(codigoQr)).thenReturn(ingressoNoBanco);

        IIngressoRepositorio repoIngresso = new IngressoRepositorioEmMemoria();
        IGatewayPagamento gateway = new GatewayPagamentoMock();
        IngressoServico servicoIngressos = new IngressoServico(repoIngresso, gateway, this);

        IngressoId idReal = IngressoId.novo();
        Ingresso ingressoReal = new Ingresso(idReal, UUID.randomUUID(), horarioEvento,
                recifecultural.dominio.ingressos.TipoIngresso.INTEIRA,
                new BigDecimal("100"), codigoQr, "TX-123", MetodoPagamento.PIX);
        repoIngresso.salvar(ingressoReal);

        servicoIngressos.solicitarReembolso(idReal, LocalDateTime.now().minusDays(5));
        Mockito.clearInvocations(repositorioMock);
    }

    @Quando("ele tenta passar a catraca do {string} às {string}")
    public void tentar_acesso(String portao, String horario) {
        LocalDateTime horaAcesso = LocalDateTime.of(LocalDate.now(), LocalTime.parse(horario));

        try {
            resultadoAcesso = servico.validarAcesso(ingressoNoBanco.getId().getValor(), horaAcesso, portao);
            excecaoCapturada = null;
        } catch (Exception e) {
            excecaoCapturada = e;
            resultadoAcesso = null;
        }
    }

    @Quando("o fraudador tenta passar a catraca do {string} às {string} com o ingresso {string}")
    public void tentar_acesso_fraudador(String portao, String horario, String codigoQr) {
        LocalDateTime horaAcesso = LocalDateTime.of(LocalDate.now(), LocalTime.parse(horario));

        try {
            resultadoAcesso = servico.validarAcesso(codigoQr, horaAcesso, portao);
            excecaoCapturada = null;
        } catch (Exception e) {
            excecaoCapturada = e;
            resultadoAcesso = null;
        }
    }

    @Então("a catraca deve exibir a mensagem {string}")
    public void verifica_sucesso(String msg) {
        Assertions.assertNull(excecaoCapturada);
        Assertions.assertEquals(msg, resultadoAcesso);
        verify(repositorioMock, times(1)).salvar(ingressoNoBanco);
    }

    @Então("o ingresso {string} deve ter o status atualizado para {string}")
    public void verifica_status(String id, String status) {
        Assertions.assertEquals(StatusIngressoCatraca.valueOf(status), ingressoNoBanco.getStatus());
    }

    @Então("o sistema deve bloquear com o erro {string}")
    public void verifica_erro(String erro) {
        Assertions.assertNotNull(excecaoCapturada);
        Assertions.assertEquals(erro, excecaoCapturada.getMessage());
        verify(repositorioMock, never()).salvar(any());
    }
}
