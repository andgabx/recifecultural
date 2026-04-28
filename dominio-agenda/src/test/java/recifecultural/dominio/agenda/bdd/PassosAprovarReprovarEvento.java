package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mockito;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.FeedbackReprovacao;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;
import recifecultural.dominio.agenda.evento.StatusEvento;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PassosAprovarReprovarEvento {

    private final ContextoAprovarReprovarEvento contexto;

    public PassosAprovarReprovarEvento(ContextoAprovarReprovarEvento contexto) {
        this.contexto = contexto;
    }

    @Dado("um evento submetido para análise")
    public void umEventoSubmetidoParaAnalise() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Peça de Teatro Clássico",
                "Apresentação no Parque Dona Lindu",
                "Descrição longa do espetáculo",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("40.00"), new BigDecimal("20.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Teatro");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
    }

    @Então("o status do evento deve ser {string}")
    public void oStatusDoEventoDeveSer(String statusEsperado) {
        assertEquals(StatusEvento.valueOf(statusEsperado), contexto.evento.getStatus());
    }

    @Quando("o gestor aprovar o evento")
    public void oGestorAprovarOEvento() {
        contexto.servicoEvento.aprovar(contexto.evento.getId());
    }

    @Dado("um evento já aprovado")
    public void umEventoJaAprovado() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Exposição de Fotografia",
                "Mostra de fotógrafos pernambucanos",
                "Descrição longa da exposição",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("20.00"), new BigDecimal("10.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Fotografia");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
        contexto.servicoEvento.aprovar(contexto.evento.getId());
    }

    @Quando("o gestor tentar aprovar o evento novamente")
    public void oGestorTentarAprovarOEventoNovamente() {
        try {
            contexto.servicoEvento.aprovar(contexto.evento.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de transição de status inválida")
    public void oSistemaDeveLancarErroDeTransicaoDeStatusInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Quando("o gestor tentar reprovar o evento com feedback vazio")
    public void oGestorTentarReprovarOEventoComFeedbackVazio() {
        try {
            contexto.servicoEvento.reprovar(contexto.evento.getId(), new FeedbackReprovacao(""));
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de feedback inválido")
    public void oSistemaDeveLancarErroFeedbackInvalido() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalArgumentException.class, contexto.excecaoCapturada);
    }

    @Quando("o gestor reprovar o evento com feedback {string}")
    public void oGestorReprovarOEventoComFeedback(String textoFeedback) {
        contexto.servicoEvento.reprovar(contexto.evento.getId(), new FeedbackReprovacao(textoFeedback));
    }

    @E("o feedback de reprovação deve estar registrado no evento")
    public void oFeedbackDeReprovacaoDeveEstarRegistradoNoEvento() {
        assertNotNull(contexto.evento.getFeedbackReprovacao());
    }

    @Dado("um evento cadastrado sem datas de apresentação")
    public void umEventoCadastradoSemDatasDeApresentacao() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz no Marco Zero",
                "Show ao vivo com artistas locais",
                "Descrição longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    @Quando("o promotor tentar submeter o evento para análise")
    public void oPromotorTentarSubmeterOEventoParaAnalise() {
        try {
            contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de submissão inválida")
    public void oSistemaDeveLancarErroDeSubmissaoInvalida() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Dado("um evento cadastrado com uma data de apresentação programada")
    public void umEventoCadastradoComUmaDataDeApresentacaoProgramada() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz no Marco Zero",
                "Show ao vivo com artistas locais",
                "Descrição longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Música");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    @Quando("o promotor submeter o evento para análise")
    public void oPromotorSubmeterOEventoParaAnalise() {
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
    }

    @Dado("um evento pronto para submissão de um promotor com 3 reprovações nos últimos 90 dias")
    public void umEventoDePromotorComReprovacoesRecentes() {
        LocalDateTime agora = LocalDateTime.now();
        UUID promotorId = UUID.randomUUID();

        contexto.evento = new Evento(
                UUID.randomUUID(), promotorId, UUID.randomUUID(),
                "Festival de Circo",
                "Espetáculo circense no Parque Dona Lindu",
                "Descrição longa do festival de circo",
                new Periodo(agora.plusDays(10), agora.plusDays(20)),
                null,
                new Preco(new BigDecimal("30.00"), new BigDecimal("15.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Circo");
        contexto.evento.programarApresentacao(agora.plusDays(12));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);

        List<Evento> reprovacoes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Evento eventoReprovado = Mockito.mock(Evento.class);
            when(eventoReprovado.getDataReprovacao()).thenReturn(agora.minusDays(10 + i * 5L));
            reprovacoes.add(eventoReprovado);
        }
        when(contexto.repositorioEvento.obterReprovacoesPorPromotor(promotorId)).thenReturn(reprovacoes);
    }

    @Dado("um evento pronto para submissão de um promotor com 3 reprovações há mais de 90 dias")
    public void umEventoDePromotorComReprovacoesAntigas() {
        LocalDateTime agora = LocalDateTime.now();
        UUID promotorId = UUID.randomUUID();

        contexto.evento = new Evento(
                UUID.randomUUID(), promotorId, UUID.randomUUID(),
                "Feira Cultural de Artesanato",
                "Mostra de artesanato pernambucano",
                "Descrição longa da feira cultural",
                new Periodo(agora.plusDays(10), agora.plusDays(20)),
                null,
                new Preco(new BigDecimal("30.00"), new BigDecimal("15.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Artesanato");
        contexto.evento.programarApresentacao(agora.plusDays(12));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);

        List<Evento> reprovacoes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Evento eventoReprovado = Mockito.mock(Evento.class);
            when(eventoReprovado.getDataReprovacao()).thenReturn(agora.minusDays(100 + i * 10L));
            reprovacoes.add(eventoReprovado);
        }
        when(contexto.repositorioEvento.obterReprovacoesPorPromotor(promotorId)).thenReturn(reprovacoes);
    }

    @Então("o sistema deve lançar um erro de bloqueio por excesso de reprovações")
    public void oSistemaDeveLancarErroDeBloqueio() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    // HU-5: artistas obrigatórios
    @Dado("um evento cadastrado com apresentação e categoria mas sem artistas")
    public void umEventoCadastradoComApresentacaoECategoriaMasSemArtistas() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz",
                "Show ao vivo com artistas locais",
                "Descrição longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        contexto.evento.definirCategoria("Música");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    // HU-6: categoria obrigatória
    @Dado("um evento cadastrado com apresentação e artistas mas sem categoria")
    public void umEventoCadastradoComApresentacaoEArtistasMasSemCategoria() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Show de Jazz",
                "Show ao vivo com artistas locais",
                "Descrição longa do evento",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    // HU-5 e HU-6 (positivo) e HU-8 (positivo): evento completo
    @Dado("um evento completo cadastrado")
    public void umEventoCompletoCadastrado() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Festival de Teatro Recife",
                "Grande festival de teatro do Nordeste",
                "Descrição longa do festival",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("45.00"), new BigDecimal("22.50"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Teatro");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    // HU-7: rascunho
    @Dado("um evento criado sem artistas, sem categoria e sem datas de apresentação")
    public void umEventoCriadoSemArtistasSemCategoriaESemDatasDeApresentacao() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Festival Cultural",
                "Evento em planejamento",
                "Descrição longa",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                null
        );
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }

    @Quando("o promotor adicionar um artista ao evento")
    public void oPromotorAdicionarUmArtistaAoEvento() {
        contexto.evento.adicionarArtista(UUID.randomUUID());
    }

    @Então("o evento deve ter pelo menos um artista registrado")
    public void oEventoDeveTerPeloMenosUmArtistaRegistrado() {
        assertFalse(contexto.evento.getArtistas().isEmpty());
    }

    // HU-8: espaço definido obrigatório
    @Dado("um evento completo cadastrado sem espaço definido")
    public void umEventoCompletoCadastradoSemEspacoDefinido() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), null,
                "Espetáculo de Dança",
                "Show de dança contemporânea",
                "Descrição longa do espetáculo",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("60.00"), new BigDecimal("30.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Dança");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
    }
}