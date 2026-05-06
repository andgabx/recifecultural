package recifecultural.dominio.compartilhado.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class PassosNotificacao {

    private final NotificacaoCenario contexto;
    private ArgumentCaptor<Notificacao> notificacaoCaptor;
    private UUID idReferenciaAtual;
    private List<UUID> usuariosBroadcast;

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

    @Então("a notificação direta deve ser salva com sucesso no repositório para o usuário")
    public void aNotificacaoDiretaDeveSerSalvaComSucessoNoRepositorioParaOUsuario() {
        Assertions.assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção");
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).salvar(notificacaoCaptor.capture());

        Notificacao salva = notificacaoCaptor.getValue();
        assertNotNull(salva);
        assertEquals(contexto.idUsuarioAtual, salva.getUsuarioAlvo());
        assertFalse(salva.isFoiLida(), "A notificação deve nascer como não lida");
    }

    @Dado("que o contexto de broadcast retornará os usuários {string} e {string}")
    public void queOContextoDeBroadcastRetornaraOsUsuarios(String id1, String id2) {
        this.usuariosBroadcast = List.of(UUID.fromString(id1), UUID.fromString(id2));

        Mockito.when(contexto.usuarioContextoServico.obterUsuariosPorContexto(Mockito.anyString(), Mockito.nullable(UUID.class)))
                .thenReturn(usuariosBroadcast);
    }

    @Quando("eu solicitar o envio de um broadcast com a mensagem {string}")
    public void euSolicitarOEnvioDeUmBroadcastComAMensagem(String mensagem) {
        try {
            contexto.servicoNotificacao.enviarBroadcast(mensagem);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve gerar e salvar notificações individuais para cada usuário retornado")
    public void oSistemaDeveGerarESalvarNotificacoesIndividuaisParaCadaUsuarioRetornado() {
        Assertions.assertNull(contexto.excecaoCapturada, "Não deveria ter lançado exceção");

        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(usuariosBroadcast.size())).salvar(notificacaoCaptor.capture());

        List<Notificacao> salvas = notificacaoCaptor.getAllValues();
        assertEquals(usuariosBroadcast.size(), salvas.size(), "O número de notificações salvas não confere");

        List<UUID> alvosSalvos = salvas.stream().map(Notificacao::getUsuarioAlvo).collect(Collectors.toList());
        assertTrue(alvosSalvos.containsAll(usuariosBroadcast), "As notificações não foram enviadas para todos os usuários do contexto");
    }

    @Dado("que o usuário {string} possui uma notificação pendente com a mensagem {string}")
    public void queOUsuarioPossuiUmaNotificacaoPendenteComAMensagem(String idUsuario, String mensagem) {
        contexto.idUsuarioAtual = UUID.fromString(idUsuario);
        contexto.notificacaoAtual = new Notificacao(contexto.idUsuarioAtual, mensagem);

        Mockito.when(contexto.repositorioNotificacao.obter(contexto.notificacaoAtual.getId())).thenReturn(contexto.notificacaoAtual);
    }

    @Quando("o usuário solicitar a marcação desta notificação como lida")
    public void oUsuarioSolicitarAMarcacaoDestaNotificacaoComoLida() {
        try {
            contexto.servicoNotificacao.marcarComoLida(contexto.notificacaoAtual.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("a notificação deve ser atualizada e constar como lida pelo sistema")
    public void aNotificacaoDeveSerAtualizadaEConstarComoLidaPeloSistema() {
        assertNull(contexto.excecaoCapturada);
        assertTrue(contexto.notificacaoAtual.isFoiLida(), "A notificação deveria estar marcada como lida");
        Mockito.verify(contexto.repositorioNotificacao, Mockito.times(1)).atualizar(contexto.notificacaoAtual);
    }

    @Dado("que o usuário {string} possui uma notificação lida com a mensagem {string}")
    public void queOUsuarioPossuiUmaNotificacaoLidaComAMensagem(String idUsuario, String mensagem) {
        contexto.idUsuarioAtual = UUID.fromString(idUsuario);
        contexto.notificacaoAtual = new Notificacao(contexto.idUsuarioAtual, mensagem);
        contexto.notificacaoAtual.marcarComoLida();

        Mockito.when(contexto.repositorioNotificacao.obter(contexto.notificacaoAtual.getId())).thenReturn(contexto.notificacaoAtual);
    }

    @Quando("o usuário solicitar a marcação desta notificação como não lida")
    public void oUsuarioSolicitarAMarcacaoDestaNotificacaoComoNaoLida() {
        try {
            contexto.servicoNotificacao.marcarComoNaoLida(contexto.notificacaoAtual.getId());
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