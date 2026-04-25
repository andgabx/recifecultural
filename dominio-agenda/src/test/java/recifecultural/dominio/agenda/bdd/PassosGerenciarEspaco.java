package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.espaco.Espaco;
import recifecultural.dominio.agenda.espaco.StatusEspaco;
import recifecultural.dominio.agenda.espaco.EspacoId;
import recifecultural.dominio.agenda.espaco.Ocupacao;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PassosGerenciarEspaco {

    private final ContextoCenario contexto;

    private String nomeInput;
    private int capacidadeInput;
    private int ingressosVendidosMock;

    private List<Ocupacao> ocupacoesExistentesMock = new ArrayList<>();

    public PassosGerenciarEspaco(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Dado("que eu informo o nome {string}")
    public void queEuInformoONome(String nome) {
        this.nomeInput = nome;
        contexto.excecaoCapturada = null;
    }

    @E("informo a capacidade maxima estipulada pelos bombeiros de {int} lugares")
    public void informoACapacidadeMaximaDeLugares(int capacidade) {
        this.capacidadeInput = capacidade;
    }

    @Quando("eu confirmo o cadastro do espaco")
    public void euConfirmoOCadastroDoEspaco() {
        try {
            contexto.idEspacoAtual = contexto.servicoEspaco.cadastrarEspaco(nomeInput, capacidadeInput, new ArrayList<>());

            contexto.espaco = new Espaco(nomeInput, capacidadeInput, new ArrayList<>());
            when(contexto.repositorioEspaco.obterPorId(any(EspacoId.class))).thenReturn(Optional.of(contexto.espaco));
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Entao("o espaco deve ser criado com o status {string}")
    public void oEspacoDeveSerCriadoComOStatus(String statusEsperado) {
        assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção");
        verify(contexto.repositorioEspaco, times(1)).salvar(any(Espaco.class));
        assertEquals(StatusEspaco.valueOf(statusEsperado), contexto.espaco.getStatus());
    }

    @Dado("que o {string} tem capacidade de {int} lugares")
    public void queOTemCapacidadeDeLugares(String nome, int capacidade) {
        contexto.espaco = new Espaco(nome, capacidade, new ArrayList<>());
        contexto.idEspacoAtual = contexto.espaco.getId();
        when(contexto.repositorioEspaco.obterPorId(contexto.idEspacoAtual)).thenReturn(Optional.of(contexto.espaco));
        contexto.excecaoCapturada = null;
    }

    @E("o evento futuro com mais vendas possui {int} ingressos vendidos")
    public void oEventoFuturoComMaisVendasPossuiIngressosVendidos(int ingressosVendidos) {
        this.ingressosVendidosMock = ingressosVendidos;
    }

    @Quando("eu tento reduzir a capacidade do teatro para {int} lugares")
    public void euTentoReduzirACapacidadeDoTeatroParaLugares(int novaCapacidade) {
        try {
            contexto.servicoEspaco.atualizarCapacidade(contexto.idEspacoAtual, novaCapacidade, ingressosVendidosMock);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Entao("a nova capacidade do teatro deve ser {int} lugares")
    public void aNovaCapacidadeDoTeatroDeveSerLugares(int capacidadeEsperada) {
        assertNull(contexto.excecaoCapturada, "Exceção inesperada ao reduzir capacidade");
        assertEquals(capacidadeEsperada, contexto.espaco.getCapacidadeMaxima());
        verify(contexto.repositorioEspaco, times(1)).atualizar(contexto.espaco);
    }

    @Dado("que o {string} esta {string}")
    public void queOEsta(String nome, String status) {
        contexto.espaco = new Espaco(nome, 500, new ArrayList<>());
        if ("INTERDITADO".equals(status)) {
            contexto.espaco.interditar();
        }
        contexto.idEspacoAtual = contexto.espaco.getId();
        when(contexto.repositorioEspaco.obterPorId(contexto.idEspacoAtual)).thenReturn(Optional.of(contexto.espaco));
        contexto.excecaoCapturada = null;
    }

    @Quando("eu solicito a interdicao do espaco")
    public void euSolicitoAInterdicaoDoEspaco() {
        try {
            contexto.servicoEspaco.interditarEspaco(contexto.idEspacoAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Entao("o status do espaco deve mudar para {string}")
    public void oStatusDoEspacoDeveMudarPara(String statusEsperado) {
        assertNull(contexto.excecaoCapturada);
        assertEquals(StatusEspaco.valueOf(statusEsperado), contexto.espaco.getStatus());
        verify(contexto.repositorioEspaco, times(1)).atualizar(contexto.espaco);
    }

    @E("não há nenhuma ocupação agendada para o dia {string}")
    public void naoHaNenhumaOcupacaoAgendadaParaODia(String data) {
        this.ocupacoesExistentesMock.clear();
        when(contexto.repositorioEspaco.buscarOcupacoesPorPeriodo(any(), any(), any()))
                .thenReturn(this.ocupacoesExistentesMock);
    }

    @E("existe uma ocupação no dia {string} das {string} às {string} com {int} min de montagem, {int} min de desmontagem e {int} min de buffer extra")
    public void existeUmaOcupacaoNoDia(String data, String horaInicio, String horaFim, int montagem, int desmontagem, int buffer) {
        LocalDateTime inicio = LocalDateTime.parse(data + "T" + horaInicio + ":00");
        LocalDateTime fim = LocalDateTime.parse(data + "T" + horaFim + ":00");

        Ocupacao ocupacao = new Ocupacao(inicio, fim, montagem, desmontagem, buffer);
        this.ocupacoesExistentesMock.add(ocupacao);

        when(contexto.repositorioEspaco.buscarOcupacoesPorPeriodo(any(), any(), any()))
                .thenReturn(this.ocupacoesExistentesMock);
    }

    @Quando("eu agendo uma pauta no dia {string} das {string} às {string} com {int} min de montagem, {int} min de desmontagem e {int} min de buffer")
    public void euAgendoUmaPautaNoDia(String data, String horaInicio, String horaFim, int montagem, int desmontagem, int buffer) {
        LocalDateTime inicio = LocalDateTime.parse(data + "T" + horaInicio + ":00");
        LocalDateTime fim = LocalDateTime.parse(data + "T" + horaFim + ":00");

        Ocupacao novaOcupacao = new Ocupacao(inicio, fim, montagem, desmontagem, buffer);

        try {
            contexto.servicoEspaco.agendarEvento(contexto.idEspacoAtual, novaOcupacao);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Entao("o agendamento deve ser salvo com sucesso")
    public void oAgendamentoDeveSerSalvoComSucesso() {
        assertNull(contexto.excecaoCapturada, "Exceção não esperada: " +
                (contexto.excecaoCapturada != null ? contexto.excecaoCapturada.getMessage() : ""));
        verify(contexto.repositorioEspaco, times(1)).salvarOcupacao(eq(contexto.idEspacoAtual), any(Ocupacao.class));
    }

    @Entao("o sistema deve bloquear a acao com uma mensagem de {string}")
    public void oSistemaDeveBloquearAAcaoComUmaMensagemDe(String trechoMensagem) {
        assertNotNull(contexto.excecaoCapturada, "Era esperada uma exceção, mas a ação passou com sucesso.");
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains(trechoMensagem),
                "Mensagem esperada: " + trechoMensagem + " | Atual: " + contexto.excecaoCapturada.getMessage());
    }
}