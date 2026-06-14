package recifecultural.dominio.agenda.sorteio;

import org.junit.jupiter.api.Test;
import recifecultural.dominio.compartilhado.notificacao.INotificacaoServico;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoId;

import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OperacaoSorteioTemplateTest {

    private Sorteio sorteioAberto(int vagas) {
        return new Sorteio(UUID.randomUUID(), UUID.randomUUID(), vagas,
                LocalDateTime.now().plusDays(5), LocalDateTime.now().plusDays(10), new Random(42));
    }

    @Test
    void inscrever_adiciona_inscricao_e_nao_notifica() {
        Sorteio sorteio = sorteioAberto(2);
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(sorteio);
        NotificacaoFake notificacao = new NotificacaoFake();
        UUID espectador = UUID.randomUUID();

        new InscreverOperacao(repositorio, notificacao, sorteio.getId(), espectador).executar();

        assertEquals(1, sorteio.getInscricoes().size());
        assertEquals(1, repositorio.atualizacoes);
        assertTrue(notificacao.notificacoes.isEmpty());
    }

    @Test
    void apurar_conclui_e_notifica_ganhadores() {
        Sorteio sorteio = sorteioAberto(1);
        sorteio.inscrever(UUID.randomUUID());
        sorteio.inscrever(UUID.randomUUID());
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(sorteio);
        NotificacaoFake notificacao = new NotificacaoFake();

        new ApurarOperacao(repositorio, notificacao, sorteio.getId()).executar();

        assertEquals(StatusSorteio.CONCLUIDO, sorteio.getStatus());
        assertEquals(1, notificacao.notificacoes.size());
        assertEquals("SORTEIO_GANHADOR", notificacao.notificacoes.get(0).contexto());
    }

    @Test
    void cancelar_faz_broadcast() {
        Sorteio sorteio = sorteioAberto(2);
        sorteio.inscrever(UUID.randomUUID());
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(sorteio);
        NotificacaoFake notificacao = new NotificacaoFake();

        new CancelarOperacao(repositorio, notificacao, sorteio.getId()).executar();

        assertEquals(StatusSorteio.CANCELADO, sorteio.getStatus());
        assertEquals(1, notificacao.broadcasts.size());
        assertEquals("SORTEIO_CANCELADO", notificacao.broadcasts.get(0).contexto());
    }

    @Test
    void desistir_de_ganhador_promove_suplente_e_notifica() {
        Sorteio sorteio = sorteioAberto(1);
        sorteio.inscrever(UUID.randomUUID());
        sorteio.inscrever(UUID.randomUUID());
        sorteio.apurar();
        UUID ganhador = sorteio.getInscricoes().stream()
                .filter(i -> i.getStatus() == StatusInscricao.GANHADOR)
                .findFirst().orElseThrow().getEspectadorId();
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(sorteio);
        NotificacaoFake notificacao = new NotificacaoFake();

        new DesistirOperacao(repositorio, notificacao, sorteio.getId(), ganhador).executar();

        assertEquals(1, sorteio.contarPorStatus(StatusInscricao.DESISTENTE));
        assertEquals(1, notificacao.notificacoes.size());
        assertEquals("SORTEIO_PROMOCAO", notificacao.notificacoes.get(0).contexto());
    }

    @Test
    void sorteio_inexistente_lanca_excecao_no_passo_buscar() {
        RepositorioFake repositorio = new RepositorioFake();
        NotificacaoFake notificacao = new NotificacaoFake();

        assertThrows(IllegalArgumentException.class,
                () -> new CancelarOperacao(repositorio, notificacao, UUID.randomUUID()).executar());
    }

    @Test
    void executar_eh_final_e_concreto() throws Exception {
        var executar = recifecultural.dominio.agenda.comum.OperacaoDominioTemplate.class.getMethod("executar");
        assertTrue(Modifier.isFinal(executar.getModifiers()));
        assertFalse(Modifier.isAbstract(executar.getModifiers()));
    }

    private static class RepositorioFake implements ISorteioRepositorio {
        private final Map<UUID, Sorteio> store = new HashMap<>();
        private int atualizacoes = 0;

        void seed(Sorteio s) { store.put(s.getId(), s); }

        @Override public void salvar(Sorteio sorteio) { store.put(sorteio.getId(), sorteio); }
        @Override public Optional<Sorteio> obter(UUID id) { return Optional.ofNullable(store.get(id)); }
        @Override public void atualizar(Sorteio sorteio) { atualizacoes++; store.put(sorteio.getId(), sorteio); }
        @Override public void deletar(UUID id) { store.remove(id); }
    }

    private record Notif(UUID usuarioAlvo, String contexto) {}
    private record Broadcast(String contexto) {}

    private static class NotificacaoFake implements INotificacaoServico {
        private final List<Notif> notificacoes = new ArrayList<>();
        private final List<Broadcast> broadcasts = new ArrayList<>();

        @Override public void enviarNotificacao(UUID usuarioAlvo, String mensagem) { notificacoes.add(new Notif(usuarioAlvo, null)); }
        @Override public void enviarNotificacao(UUID usuarioAlvo, String mensagem, String contexto, UUID idReferencia) { notificacoes.add(new Notif(usuarioAlvo, contexto)); }
        @Override public void enviarBroadcast(String mensagem) { broadcasts.add(new Broadcast(null)); }
        @Override public void enviarBroadcast(String mensagem, String contexto, UUID idReferencia) { broadcasts.add(new Broadcast(contexto)); }
        @Override public void marcarComoLida(NotificacaoId id) {}
        @Override public void marcarComoNaoLida(NotificacaoId id) {}
        @Override public List<Notificacao> obterNotificacoesDoUsuario(UUID usuarioAlvo) { return List.of(); }
        @Override public List<Notificacao> obterNotificacoesNaoLidas(UUID usuarioAlvo) { return List.of(); }
    }
}
