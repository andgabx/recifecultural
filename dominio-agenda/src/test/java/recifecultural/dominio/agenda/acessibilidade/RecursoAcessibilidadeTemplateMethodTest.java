package recifecultural.dominio.agenda.acessibilidade;

import org.junit.jupiter.api.Test;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoId;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RecursoAcessibilidadeTemplateMethodTest {

    private static final String JUSTIFICATIVA = "Recurso indisponivel por motivo tecnico confirmado";

    @Test
    void executar_aplica_regra_persiste_e_notifica() {
        RecursoAcessibilidade recurso = new RecursoAcessibilidade(
                UUID.randomUUID(), UUID.randomUUID(), TipoRecursoAcessibilidade.LIBRAS);
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(recurso);
        NotificacaoFake notificacao = new NotificacaoFake();

        new RemocaoRecursoAcessibilidadeOperacao(repositorio, notificacao, recurso.getId(), JUSTIFICATIVA)
                .executar();

        assertEquals(StatusRecurso.REMOVIDO, recurso.getStatus());
        assertEquals(JUSTIFICATIVA, recurso.getJustificativaRemocao());
        assertEquals(1, repositorio.atualizacoes);
        assertEquals(1, notificacao.broadcasts.size());
        assertEquals("ACESSIBILIDADE_REMOVIDA", notificacao.broadcasts.get(0).contexto());
    }

    @Test
    void recurso_inexistente_lanca_excecao_no_passo_buscar() {
        RepositorioFake repositorio = new RepositorioFake();
        NotificacaoFake notificacao = new NotificacaoFake();

        assertThrows(IllegalArgumentException.class,
                () -> new RemocaoRecursoAcessibilidadeOperacao(
                        repositorio, notificacao, UUID.randomUUID(), JUSTIFICATIVA).executar());
    }

    @Test
    void executar_eh_final_e_concreto() throws Exception {
        var executar = OperacaoRecursoAcessibilidadeTemplate.class.getMethod("executar");
        assertTrue(Modifier.isFinal(executar.getModifiers()));
        assertFalse(Modifier.isAbstract(executar.getModifiers()));
    }

    private static class RepositorioFake implements IRecursoAcessibilidadeRepositorio {
        private final Map<UUID, RecursoAcessibilidade> store = new HashMap<>();
        private int atualizacoes = 0;

        void seed(RecursoAcessibilidade r) { store.put(r.getId(), r); }

        @Override public void salvar(RecursoAcessibilidade recurso) { store.put(recurso.getId(), recurso); }
        @Override public Optional<RecursoAcessibilidade> obter(UUID id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<RecursoAcessibilidade> listarPorApresentacao(UUID apresentacaoId) { return List.of(); }
        @Override public List<RecursoAcessibilidade> listarAtivosPorEvento(UUID eventoId) { return List.of(); }
        @Override public List<RecursoAcessibilidade> listarPorEvento(UUID eventoId) { return List.of(); }
        @Override public List<RecursoAcessibilidade> listarTodos() { return List.copyOf(store.values()); }
        @Override public void atualizar(RecursoAcessibilidade recurso) { atualizacoes++; store.put(recurso.getId(), recurso); }
    }

    private record Broadcast(String mensagem, String contexto, UUID idReferencia) {}

    private static class NotificacaoFake implements INotificacaoServico {
        private final List<Broadcast> broadcasts = new ArrayList<>();

        @Override public void enviarNotificacao(UUID usuarioAlvo, String mensagem) {}
        @Override public void enviarNotificacao(UUID usuarioAlvo, String mensagem, String contexto, UUID idReferencia) {}
        @Override public void enviarBroadcast(String mensagem) { broadcasts.add(new Broadcast(mensagem, null, null)); }
        @Override public void enviarBroadcast(String mensagem, String contexto, UUID idReferencia) { broadcasts.add(new Broadcast(mensagem, contexto, idReferencia)); }
        @Override public void marcarComoLida(NotificacaoId id) {}
        @Override public void marcarComoNaoLida(NotificacaoId id) {}
        @Override public List<Notificacao> obterNotificacoesDoUsuario(UUID usuarioAlvo) { return List.of(); }
        @Override public List<Notificacao> obterNotificacoesNaoLidas(UUID usuarioAlvo) { return List.of(); }
    }
}
