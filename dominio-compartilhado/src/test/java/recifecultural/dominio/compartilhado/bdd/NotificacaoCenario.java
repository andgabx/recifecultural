package recifecultural.dominio.compartilhado.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.IUsuarioContextoServico;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoServico;

import java.util.UUID;

public class NotificacaoCenario {
    public Exception excecaoCapturada;

    public UUID idUsuarioAtual;
    public Notificacao notificacaoAtual;

    public INotificacaoRepositorio repositorioNotificacao = Mockito.mock(INotificacaoRepositorio.class);
    public IUsuarioContextoServico usuarioContextoServico = Mockito.mock(IUsuarioContextoServico.class);

    public NotificacaoServico servicoNotificacao = Mockito.spy(new NotificacaoServico(repositorioNotificacao, usuarioContextoServico));
}