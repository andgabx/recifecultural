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
import static org.junit.jupiter.api.Assertions.assertTrue;
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

    // artistas obrigatórios
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

    // categoria obrigatória
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

    // evento completo
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

    // evento definido como rascunho
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

    // conflito de espaço
    @Dado("um evento submetido para análise em espaço ocupado por evento aprovado no mesmo período")
    public void umEventoSubmetidoEmEspacoOcupado() {
        LocalDateTime agora = LocalDateTime.now();
        UUID localId = UUID.randomUUID();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), localId,
                "Sarau Literário",
                "Encontro de poetas e escritores",
                "Descrição longa do sarau",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("20.00"), new BigDecimal("10.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Literatura");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());

        Evento conflitante = Mockito.mock(Evento.class);
        when(conflitante.getId()).thenReturn(UUID.randomUUID());
        when(conflitante.getStatus()).thenReturn(StatusEvento.APROVADO);
        when(contexto.repositorioEvento.obterPorLocalEIntervalo(any(), any(), any()))
                .thenReturn(List.of(conflitante));
    }

    @Quando("o gestor tentar aprovar o evento")
    public void oGestorTentarAprovarOEvento() {
        try {
            contexto.servicoEvento.aprovar(contexto.evento.getId());
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de conflito de espaço")
    public void oSistemaDeveLancarErroDeConflitoDeEspaco() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Dado("um evento submetido para análise em espaço disponível no período")
    public void umEventoSubmetidoEmEspacoDisponivel() {
        LocalDateTime agora = LocalDateTime.now();
        contexto.evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Mostra de Cinema",
                "Exibição de filmes independentes",
                "Descrição longa da mostra",
                new Periodo(agora.plusDays(1), agora.plusDays(5)),
                null,
                new Preco(new BigDecimal("15.00"), new BigDecimal("7.50"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Cinema");
        contexto.evento.programarApresentacao(agora.plusDays(2));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());
    }

    // limite de eventos aprovados por promotor
    @Dado("um evento submetido para análise de um promotor com 5 eventos já aprovados")
    public void umEventoDePromotorComLimiteAtingido() {
        LocalDateTime agora = LocalDateTime.now();
        UUID promotorId = UUID.randomUUID();
        contexto.evento = new Evento(
                UUID.randomUUID(), promotorId, UUID.randomUUID(),
                "Espetáculo de Dança Contemporânea",
                "Apresentação de grupo local",
                "Descrição longa do espetáculo",
                new Periodo(agora.plusDays(5), agora.plusDays(10)),
                null,
                new Preco(new BigDecimal("35.00"), new BigDecimal("17.50"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Dança");
        contexto.evento.programarApresentacao(agora.plusDays(6));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());

        List<Evento> aprovados = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Evento aprovado = Mockito.mock(Evento.class);
            when(aprovado.getStatus()).thenReturn(StatusEvento.APROVADO);
            aprovados.add(aprovado);
        }
        when(contexto.repositorioEvento.obterEventosAprovadosPorPromotor(promotorId)).thenReturn(aprovados);
    }

    @Então("o sistema deve lançar um erro de limite de eventos aprovados atingido")
    public void oSistemaDeveLancarErroDeLimiteDeEventosAprovados() {
        assertNotNull(contexto.excecaoCapturada);
        assertInstanceOf(IllegalStateException.class, contexto.excecaoCapturada);
    }

    @Dado("um evento submetido para análise de um promotor com 4 eventos já aprovados")
    public void umEventoDePromotorAbaixoDoLimite() {
        LocalDateTime agora = LocalDateTime.now();
        UUID promotorId = UUID.randomUUID();
        contexto.evento = new Evento(
                UUID.randomUUID(), promotorId, UUID.randomUUID(),
                "Festival de Jazz",
                "Apresentações ao vivo no centro da cidade",
                "Descrição longa do festival",
                new Periodo(agora.plusDays(5), agora.plusDays(10)),
                null,
                new Preco(new BigDecimal("50.00"), new BigDecimal("25.00"), null)
        );
        contexto.evento.adicionarArtista(UUID.randomUUID());
        contexto.evento.definirCategoria("Música");
        contexto.evento.programarApresentacao(agora.plusDays(6));
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);
        contexto.servicoEvento.submeterParaAnalise(contexto.evento.getId());

        List<Evento> aprovados = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Evento aprovado = Mockito.mock(Evento.class);
            when(aprovado.getStatus()).thenReturn(StatusEvento.APROVADO);
            aprovados.add(aprovado);
        }
        when(contexto.repositorioEvento.obterEventosAprovadosPorPromotor(promotorId)).thenReturn(aprovados);
    }

    // taxa histórica de aprovação
    @Dado("um evento pronto para submissão de um promotor com taxa de aprovação de 20% nos últimos 12 meses")
    public void umEventoDePromotorComBaixaTaxaDeAprovacao() {
        LocalDateTime agora = LocalDateTime.now();
        UUID promotorId = UUID.randomUUID();
        contexto.evento = criarEventoCompleto(promotorId, agora, "Circo Contemporâneo", "Circo");
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);

        // 5 finalizados: 1 aprovado (20%) e 4 reprovados — todos nos últimos 12 meses
        List<Evento> finalizados = new ArrayList<>();
        Evento aprovado = Mockito.mock(Evento.class);
        when(aprovado.getStatus()).thenReturn(StatusEvento.APROVADO);
        when(aprovado.getDataAprovacao()).thenReturn(agora.minusMonths(3));
        finalizados.add(aprovado);
        for (int i = 0; i < 4; i++) {
            Evento reprovado = Mockito.mock(Evento.class);
            when(reprovado.getStatus()).thenReturn(StatusEvento.REPROVADO);
            when(reprovado.getDataReprovacao()).thenReturn(agora.minusMonths(2).minusDays(i * 10L));
            finalizados.add(reprovado);
        }
        when(contexto.repositorioEvento.obterEventosFinalizadosPorPromotor(promotorId)).thenReturn(finalizados);
    }

    @Então("o evento deve estar marcado como requer revisão adicional")
    public void oEventoDeveEstarMarcadoComoRequerRevisaoAdicional() {
        assertTrue(contexto.evento.isRequerRevisaoAdicional());
    }

    @Dado("um evento pronto para submissão de um promotor com taxa de aprovação de 60% nos últimos 12 meses")
    public void umEventoDePromotorComBoaTaxaDeAprovacao() {
        LocalDateTime agora = LocalDateTime.now();
        UUID promotorId = UUID.randomUUID();
        contexto.evento = criarEventoCompleto(promotorId, agora, "Ópera ao Ar Livre", "Música Clássica");
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);

        // 5 finalizados: 3 aprovados (60%) e 2 reprovados — todos nos últimos 12 meses
        List<Evento> finalizados = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Evento aprovado = Mockito.mock(Evento.class);
            when(aprovado.getStatus()).thenReturn(StatusEvento.APROVADO);
            when(aprovado.getDataAprovacao()).thenReturn(agora.minusMonths(2).minusDays(i * 15L));
            finalizados.add(aprovado);
        }
        for (int i = 0; i < 2; i++) {
            Evento reprovado = Mockito.mock(Evento.class);
            when(reprovado.getStatus()).thenReturn(StatusEvento.REPROVADO);
            when(reprovado.getDataReprovacao()).thenReturn(agora.minusMonths(4).minusDays(i * 10L));
            finalizados.add(reprovado);
        }
        when(contexto.repositorioEvento.obterEventosFinalizadosPorPromotor(promotorId)).thenReturn(finalizados);
    }

    @Então("o evento não deve estar marcado como requer revisão adicional")
    public void oEventoNaoDeveEstarMarcadoComoRequerRevisaoAdicional() {
        assertFalse(contexto.evento.isRequerRevisaoAdicional());
    }

    @Dado("um evento pronto para submissão de um promotor com apenas 3 eventos finalizados nos últimos 12 meses")
    public void umEventoDePromotorComHistoricoInsuficiente() {
        LocalDateTime agora = LocalDateTime.now();
        UUID promotorId = UUID.randomUUID();
        contexto.evento = criarEventoCompleto(promotorId, agora, "Feira Gastronômica Cultural", "Gastronomia");
        when(contexto.repositorioEvento.obter(any())).thenReturn(Optional.of(contexto.evento));
        contexto.servicoEvento.salvar(contexto.evento);

        // apenas 3 finalizados — abaixo do mínimo de 5 para ativar a regra
        List<Evento> finalizados = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Evento reprovado = Mockito.mock(Evento.class);
            when(reprovado.getStatus()).thenReturn(StatusEvento.REPROVADO);
            when(reprovado.getDataReprovacao()).thenReturn(agora.minusMonths(1).minusDays(i * 10L));
            finalizados.add(reprovado);
        }
        when(contexto.repositorioEvento.obterEventosFinalizadosPorPromotor(promotorId)).thenReturn(finalizados);
    }

    private Evento criarEventoCompleto(UUID promotorId, LocalDateTime agora, String titulo, String categoria) {
        Evento evento = new Evento(
                UUID.randomUUID(), promotorId, UUID.randomUUID(),
                titulo,
                "Descrição curta do evento",
                "Descrição longa detalhada do evento cultural",
                new Periodo(agora.plusDays(10), agora.plusDays(15)),
                null,
                new Preco(new BigDecimal("30.00"), new BigDecimal("15.00"), null)
        );
        evento.adicionarArtista(UUID.randomUUID());
        evento.definirCategoria(categoria);
        evento.programarApresentacao(agora.plusDays(12));
        return evento;
    }

    // espaço definido obrigatório
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