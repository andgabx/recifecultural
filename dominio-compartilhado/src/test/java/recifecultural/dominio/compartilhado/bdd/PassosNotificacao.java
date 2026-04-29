package recifecultural.dominio.compartilhado.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PassosNotificacao {

    private final NotificacaoCenario contexto;
    private ArgumentCaptor<Notificacao> notificacaoCaptor;
    private UUID idReferenciaAtual; // Utilizado no teste de contexto/referência

    public PassosNotificacao(NotificacaoCenario contexto) {
        this.contexto = contexto;
        this.notificacaoCaptor = ArgumentCaptor.forClass(Notificacao.class);
    }

    @Dado("que existe um usuário alvo com ID {string}")
    public void queExisteUmUsuarioAlvoComId(String idUsuario) {
        contexto.idUsuarioAtual = UUID.fromString(idUsuario);
        contexto.excecaoCapturada = null;
    }

    @Quando("eu solicitar o envio de uma notificação com a mensagem {string}")
    public void euSolicitarOEnvioDeUmaNotificacaoComAMensagem(String mensagem) {
        try {
            contexto.servicoNotificacao.enviarNotificacao(contexto.idUsuarioAtual, mensagem);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("a notificação direta deve ser salva com sucesso no repositório")
    public void aNotificacaoDiretaDeveSerSalvaComSucessoNoRepositorio() {
        Assertions.assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção");
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).salvar(notificacaoCaptor.capture());

        Notificacao salva = notificacaoCaptor.getValue();
        assertNotNull(salva);
        assertFalse(salva.isBroadcast(), "A notificação deveria ser direta, não broadcast");
        assertEquals(contexto.idUsuarioAtual, salva.getUsuarioAlvo());
    }

    @Quando("eu solicitar o envio de um broadcast com a mensagem {string}")
    public void euSolicitarOEnvioDeUmBroadcastComAMensagem(String mensagem) {
        try {
            contexto.servicoNotificacao.enviarBroadcast(mensagem);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("a notificação de broadcast deve ser salva com sucesso no repositório")
    public void aNotificacaoDeBroadcastDeveSerSalvaComSucessoNoRepositorio() {
        Assertions.assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção");
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).salvar(notificacaoCaptor.capture());

        Notificacao salva = notificacaoCaptor.getValue();
        assertNotNull(salva);
        assertTrue(salva.isBroadcast(), "A notificação deveria ser um broadcast");
        assertNull(salva.getUsuarioAlvo(), "Broadcasts não possuem um usuário alvo único");
    }

    @Dado("que o usuário {string} possui uma notificação direta pendente com a mensagem {string}")
    public void queOUsuarioPossuiUmaNotificacaoDiretaPendenteComAMensagem(String idUsuario, String mensagem) {
        contexto.idUsuarioAtual = UUID.fromString(idUsuario);
        contexto.notificacaoAtual = new Notificacao(contexto.idUsuarioAtual, mensagem);

        Mockito.when(contexto.repositorioNotificacao.obter(contexto.notificacaoAtual.getId())).thenReturn(contexto.notificacaoAtual);
    }

    @Quando("o usuário solicitar a marcação desta notificação como lida")
    public void oUsuarioSolicitarAMarcacaoDestaNotificacaoComoLida() {
        try {
            contexto.servicoNotificacao.marcarComoLida(contexto.notificacaoAtual.getId(), contexto.idUsuarioAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("a notificação deve ser atualizada e constar como lida pelo sistema")
    public void aNotificacaoDeveSerAtualizadaEConstarComoLidaPeloSistema() {
        assertNull(contexto.excecaoCapturada);
        assertTrue(contexto.notificacaoAtual.isFoiLida(), "A notificação direta deveria estar marcada como lida");
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).atualizar(contexto.notificacaoAtual);
    }

    @Dado("que existe um broadcast pendente com a mensagem {string}")
    public void queExisteUmBroadcastPendenteComAMensagem(String mensagem) {
        contexto.notificacaoAtual = Notificacao.criarBroadcast(mensagem);

        Mockito.when(contexto.repositorioNotificacao.obter(contexto.notificacaoAtual.getId())).thenReturn(contexto.notificacaoAtual);
    }

    @E("um usuário leitor com ID {string}")
    public void umUsuarioLeitorComID(String idUsuario) {
        contexto.idUsuarioAtual = UUID.fromString(idUsuario);
    }

    @Quando("este usuário leitor solicitar a marcação do broadcast como lido")
    public void esteUsuarioLeitorSolicitarAMarcacaoDoBroadcastComoLido() {
        try {
            contexto.servicoNotificacao.marcarComoLida(contexto.notificacaoAtual.getId(), contexto.idUsuarioAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o broadcast deve registrar a leitura exclusivamente para este usuário")
    public void oBroadcastDeveRegistrarALeituraExclusivamenteParaEsteUsuario() {
        assertNull(contexto.excecaoCapturada);
        assertTrue(contexto.notificacaoAtual.isBroadcast(), "Deveria ser um broadcast");
        assertTrue(contexto.notificacaoAtual.isLidaPor(contexto.idUsuarioAtual), "Broadcast deveria estar lido pelo usuário especificado");
        assertTrue(contexto.notificacaoAtual.getLidaPor().contains(contexto.idUsuarioAtual));
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).atualizar(contexto.notificacaoAtual);
    }

    @Dado("que o usuário {string} possui uma notificação direta lida com a mensagem {string}")
    public void queOUsuarioPossuiUmaNotificacaoDiretaLidaComAMensagem(String idUsuario, String mensagem) {
        contexto.idUsuarioAtual = UUID.fromString(idUsuario);
        contexto.notificacaoAtual = new Notificacao(contexto.idUsuarioAtual, mensagem);
        contexto.notificacaoAtual.marcarComoLida(contexto.idUsuarioAtual);

        Mockito.when(contexto.repositorioNotificacao.obter(contexto.notificacaoAtual.getId())).thenReturn(contexto.notificacaoAtual);
    }

    @Quando("o usuário solicitar a marcação desta notificação como não lida")
    public void oUsuarioSolicitarAMarcacaoDestaNotificacaoComoNaoLida() {
        try {
            contexto.servicoNotificacao.marcarComoNaoLida(contexto.notificacaoAtual.getId(), contexto.idUsuarioAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("a notificação deve ser atualizada e constar como não lida pelo sistema")
    public void aNotificacaoDeveSerAtualizadaEConstarComoNaoLidaPeloSistema() {
        assertNull(contexto.excecaoCapturada);
        assertFalse(contexto.notificacaoAtual.isFoiLida(), "A notificação deveria constar como não lida");
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).atualizar(contexto.notificacaoAtual);
    }

    @Dado("um evento de referência com ID {string}")
    public void umEventoDeReferenciaComID(String idReferencia) {
        this.idReferenciaAtual = UUID.fromString(idReferencia);
    }

    @Quando("eu solicitar o envio de uma notificação com a mensagem {string}, contexto {string} e referência do evento")
    public void euSolicitarOEnvioDeUmaNotificacaoComAMensagemContextoEReferenciaDoEvento(String mensagem, String contextoNotificacao) {
        try {
            contexto.servicoNotificacao.enviarNotificacao(contexto.idUsuarioAtual, mensagem, contextoNotificacao, this.idReferenciaAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("a notificação deve ser salva contendo o contexto {string} e a referência correta")
    public void aNotificacaoDeveSerSalvaContendoOContextoEAReferenciaCorreta(String contextoEsperado) {
        Assertions.assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção");
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).salvar(notificacaoCaptor.capture());

        Notificacao salva = notificacaoCaptor.getValue();
        assertNotNull(salva);
        assertEquals(contextoEsperado, salva.getContexto());
        assertEquals(this.idReferenciaAtual, salva.getIdReferencia());
    }
}