package recifecultural.dominio.agenda.bdd;

import org.mockito.Mockito;
import recifecultural.dominio.agenda.acessibilidade.IRecursoAcessibilidadeRepositorio;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidade;
import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidadeServico;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.IUsuarioContextoServico;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoServico;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ContextoAcessibilidade {
    public Evento evento;
    public UUID apresentacaoId;
    public RecursoAcessibilidade recurso;
    public Exception excecaoCapturada;
    public List<RecursoAcessibilidade> recursosDaApresentacao = new ArrayList<>();

    public IEventoRepositorio eventoRepositorio = Mockito.mock(IEventoRepositorio.class);
    public IRecursoAcessibilidadeRepositorio recursoRepositorio = Mockito.mock(IRecursoAcessibilidadeRepositorio.class);
    public NotificacaoServico notificacaoServico = new NotificacaoServico(
            Mockito.mock(INotificacaoRepositorio.class), Mockito.mock(IUsuarioContextoServico.class));
    public RecursoAcessibilidadeServico servico =
            new RecursoAcessibilidadeServico(recursoRepositorio, eventoRepositorio, notificacaoServico);
}
