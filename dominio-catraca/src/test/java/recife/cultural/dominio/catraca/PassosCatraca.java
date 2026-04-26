package recife.cultural.dominio.catraca;

import io.cucumber.java.pt.*;
import org.junit.jupiter.api.Assertions;
import org.mockito.Mockito;
import recifecultural.dominio.catraca.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PassosCatraca {

    // Dependências Mockadas
    private ICatracaRepositorio repositorioMock = mock(ICatracaRepositorio.class);
    private CatracaServico servico = new CatracaServico(repositorioMock);

    // Estado do Teste
    private IngressoCatraca ingressoNoBanco;
    private LocalDateTime horarioEvento;
    private Exception excecaoCapturada;
    private String resultadoAcesso;

    @Dado("que o evento {string} começa às {string}")
    public void que_o_evento_comeca_as(String nome, String horario) {
        horarioEvento = LocalDateTime.of(LocalDate.now(), LocalTime.parse(horario));
    }

    @Dado("que o espectador possui o ingresso {string} com status {string}")
    @Dado("que o ingresso {string} já possui o status {string}")
    @Dado("que o ingresso {string} foi devolvido e tem status {string}")
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

    @Quando("ele tenta passar a catraca do {string} às {string}")
    @Quando("o fraudador tenta passar a catraca com o ingresso {string} às {string}")
    public void tentar_acesso(String portaoOuId, String horarioOuPortao) {
        String portaoFisico = horarioOuPortao.contains(":") ? portaoOuId : horarioOuPortao;
        String horarioStr = horarioOuPortao.contains(":") ? horarioOuPortao : portaoOuId;

        if (horarioStr.length() < 5) horarioStr = portaoOuId; // fallback para IDs curtos

        LocalDateTime horaAcesso = LocalDateTime.of(LocalDate.now(), LocalTime.parse(horarioStr));

        try {
            resultadoAcesso = servico.validarAcesso(ingressoNoBanco.getId().getValor(), horaAcesso, portaoFisico);
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
        // Garante que o Mockito NÃO salvou nada se a regra falhou
        verify(repositorioMock, never()).salvar(any());
    }
}