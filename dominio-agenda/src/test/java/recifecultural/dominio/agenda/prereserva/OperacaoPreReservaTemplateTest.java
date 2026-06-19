package recifecultural.dominio.agenda.prereserva;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OperacaoPreReservaTemplateTest {

    private static final LocalDateTime AGORA = LocalDateTime.of(2026, 6, 19, 12, 0);
    private static final LocalDateTime EXPIRADA_EM = AGORA.minusMinutes(1);

    private PreReserva preReservaAtiva() {
        return new PreReserva(
                PreReservaId.novo(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                AGORA.minusMinutes(15),
                AGORA.plusMinutes(10),
                StatusPreReserva.ATIVA,
                0
        );
    }

    private PreReserva preReservaAtivaExpirada() {
        return new PreReserva(
                PreReservaId.novo(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                AGORA.minusMinutes(20),
                EXPIRADA_EM,
                StatusPreReserva.ATIVA,
                0
        );
    }

    @Test
    void confirmar_transicao_ativa_para_convertida() {
        PreReserva preReserva = preReservaAtiva();
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(preReserva);

        new ConfirmarPreReservaOperacao(repositorio).executar(preReserva.getId());

        assertEquals(StatusPreReserva.CONVERTIDA, preReserva.getStatus());
    }

    @Test
    void confirmar_pre_reserva_nao_encontrada_lanca_excecao() {
        RepositorioFake repositorio = new RepositorioFake();
        PreReservaId idInexistente = PreReservaId.novo();

        assertThrows(IllegalArgumentException.class,
                () -> new ConfirmarPreReservaOperacao(repositorio).executar(idInexistente));
    }

    @Test
    void cancelar_transicao_ativa_para_cancelada() {
        PreReserva preReserva = preReservaAtiva();
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(preReserva);

        new CancelarPreReservaOperacao(repositorio).executar(preReserva.getId());

        assertEquals(StatusPreReserva.CANCELADA, preReserva.getStatus());
    }

    @Test
    void cancelar_pre_reserva_ja_cancelada_lanca_excecao() {
        PreReserva preReserva = new PreReserva(
                PreReservaId.novo(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                AGORA.minusMinutes(15),
                AGORA.plusMinutes(10),
                StatusPreReserva.CANCELADA,
                0
        );
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(preReserva);

        assertThrows(IllegalStateException.class,
                () -> new CancelarPreReservaOperacao(repositorio).executar(preReserva.getId()));
    }

    @Test
    void expirar_transicao_ativa_para_expirada() {
        PreReserva preReserva = preReservaAtivaExpirada();
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(preReserva);

        new ExpirarPreReservaOperacaoComHoraFixa(repositorio, AGORA).executar(preReserva.getId());

        assertEquals(StatusPreReserva.EXPIRADA, preReserva.getStatus());
    }

    @Test
    void expirar_pre_reserva_com_objeto_direto() {
        PreReserva preReserva = preReservaAtivaExpirada();
        RepositorioFake repositorio = new RepositorioFake();

        new ExpirarPreReservaOperacaoComHoraFixa(repositorio, AGORA).executar(preReserva);

        assertEquals(StatusPreReserva.EXPIRADA, preReserva.getStatus());
    }

    @Test
    void executar_chama_persistir_apos_transicao() {
        PreReserva preReserva = preReservaAtiva();
        RepositorioFake repositorio = new RepositorioFake();
        repositorio.seed(preReserva);

        new ConfirmarPreReservaOperacao(repositorio).executar(preReserva.getId());

        assertEquals(1, repositorio.atualizacoes);
    }

    private static class RepositorioFake implements IPreReservaRepositorio {
        private final Map<UUID, PreReserva> store = new HashMap<>();
        int atualizacoes = 0;

        void seed(PreReserva pr) { store.put(pr.getId().valor(), pr); }

        @Override public void salvar(PreReserva preReserva) { store.put(preReserva.getId().valor(), preReserva); }
        @Override public void atualizar(PreReserva preReserva) { atualizacoes++; store.put(preReserva.getId().valor(), preReserva); }
        @Override public Optional<PreReserva> obterPorId(PreReservaId id) { return Optional.ofNullable(store.get(id.valor())); }
        @Override public List<PreReserva> listarAtivasPorAssentoEEvento(UUID assentoId, UUID eventoId) { return List.of(); }
        @Override public List<PreReserva> listarAtivasPorEvento(UUID eventoId) { return List.of(); }
        @Override public List<PreReserva> listarAtivasExpiradas(LocalDateTime agora) { return List.of(); }
    }

    private static class ExpirarPreReservaOperacaoComHoraFixa extends ExpirarPreReservaOperacao {
        private final LocalDateTime horaFixa;

        ExpirarPreReservaOperacaoComHoraFixa(IPreReservaRepositorio repositorio, LocalDateTime horaFixa) {
            super(repositorio);
            this.horaFixa = horaFixa;
        }

        @Override
        protected java.time.LocalDateTime agora() {
            return horaFixa;
        }
    }
}
