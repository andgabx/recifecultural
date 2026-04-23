package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.espaco.Espaco;
import recifecultural.dominio.agenda.espaco.StatusEspaco;
import recifecultural.dominio.agenda.espaco.EspacoId;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PassosGerenciarEspaco {

    private final ContextoCenario contexto;

    private String nomeInput;
    private int capacidadeInput;
    private int ingressosVendidosMock;

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

            // Simulando o salvamento para os asserts futuros
            contexto.espaco = new Espaco(nomeInput, capacidadeInput, new ArrayList<>());
            when(contexto.repositorioEspaco.obterPorId(any(EspacoId.class))).thenReturn(Optional.of(contexto.espaco));

        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o espaco deve ser criado com o status {string}")
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

    @Então("a nova capacidade do teatro deve ser {int} lugares")
    public void aNovaCapacidadeDoTeatroDeveSerLugares(int capacidadeEsperada) {
        assertNull(contexto.excecaoCapturada);
        assertEquals(capacidadeEsperada, contexto.espaco.getCapacidadeMaxima());
        verify(contexto.repositorioEspaco, times(1)).atualizar(contexto.espaco);
    }

    @Então("o sistema deve bloquear a acao com uma mensagem de {string}")
    public void oSistemaDeveBloquearAAcaoComUmaMensagemDe(String trechoMensagem) {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains(trechoMensagem));
    }

    @Dado("que o {string} esta {string}")
    public void queOEsta(String nome, String status) {
        contexto.espaco = new Espaco(nome, 500, new ArrayList<>());
        contexto.idEspacoAtual = contexto.espaco.getId();
        when(contexto.repositorioEspaco.obterPorId(contexto.idEspacoAtual)).thenReturn(Optional.of(contexto.espaco));
    }

    @Quando("eu solicito a interdicao do espaco")
    public void euSolicitoAInterdicaoDoEspaco() {
        try {
            contexto.servicoEspaco.interditarEspaco(contexto.idEspacoAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o status do espaco deve mudar para {string}")
    public void oStatusDoEspacoDeveMudarPara(String statusEsperado) {
        assertNull(contexto.excecaoCapturada);
        assertEquals(StatusEspaco.valueOf(statusEsperado), contexto.espaco.getStatus());
        verify(contexto.repositorioEspaco, times(1)).atualizar(contexto.espaco);
    }
}