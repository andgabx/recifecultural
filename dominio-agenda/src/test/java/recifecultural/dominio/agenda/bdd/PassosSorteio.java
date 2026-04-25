package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;
import recifecultural.dominio.agenda.sorteio.Sorteio;
import recifecultural.dominio.agenda.sorteio.StatusInscricao;
import recifecultural.dominio.agenda.sorteio.StatusSorteio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PassosSorteio {

    private final ContextoSorteio contexto;

    public PassosSorteio(ContextoSorteio contexto) {
        this.contexto = contexto;
    }

    @Dado("um evento em análise pronto para sorteio")
    public void umEventoEmAnaliseProntoParaSorteio() {
        contexto.evento = criarEventoBase();
        contexto.evento.submeterParaAnalise();
        when(contexto.eventoRepositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
    }

    @Quando("o gestor tentar abrir um sorteio com {int} vagas")
    public void oGestorTentarAbrirUmSorteioComVagas(Integer vagas) {
        try {
            LocalDateTime dataApresentacao = contexto.evento.getDatasApresentacao().get(0);
            contexto.sorteio = contexto.servico.criar(
                    UUID.randomUUID(),
                    contexto.evento.getId(),
                    vagas,
                    dataApresentacao.minusDays(1),
                    dataApresentacao
            );
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sistema deve lançar um erro de regra do sorteio")
    public void oSistemaDeveLancarUmErroDeRegraDoSorteio() {
        assertNotNull(contexto.excecaoCapturada);
        boolean ehRegra = contexto.excecaoCapturada instanceof IllegalStateException
                || contexto.excecaoCapturada instanceof IllegalArgumentException;
        assertEquals(true, ehRegra, "Esperava IllegalState/Argument, veio " + contexto.excecaoCapturada);
    }

    @Dado("um sorteio aberto com {int} vagas")
    public void umSorteioAbertoComVagas(Integer vagas) {
        contexto.evento = criarEventoAprovado();
        when(contexto.eventoRepositorio.obter(any())).thenReturn(Optional.of(contexto.evento));

        LocalDateTime dataApresentacao = contexto.evento.getDatasApresentacao().get(0);
        contexto.sorteio = contexto.servico.criar(
                UUID.randomUUID(),
                contexto.evento.getId(),
                vagas,
                dataApresentacao.minusDays(1),
                dataApresentacao
        );
        when(contexto.sorteioRepositorio.obter(any())).thenReturn(Optional.of(contexto.sorteio));
    }

    @Quando("o espectador {string} se inscreve no sorteio")
    public void oEspectadorSeInscreveNoSorteio(String nome) {
        UUID id = idDe(nome);
        contexto.servico.inscrever(contexto.sorteio.getId(), id);
    }

    @E("o espectador {string} já está inscrito")
    public void oEspectadorJaEstaInscrito(String nome) {
        UUID id = idDe(nome);
        contexto.servico.inscrever(contexto.sorteio.getId(), id);
    }

    @Quando("o espectador {string} tenta se inscrever novamente")
    public void oEspectadorTentaSeInscreverNovamente(String nome) {
        try {
            contexto.servico.inscrever(contexto.sorteio.getId(), idDe(nome));
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Então("o sorteio deve ter {int} inscrição")
    public void oSorteioDeveTerInscricao(Integer total) {
        assertEquals(total, contexto.sorteio.getInscricoes().size());
    }

    @E("{int} espectadores se inscreveram no sorteio")
    public void espectadoresSeInscreveramNoSorteio(Integer quantidade) {
        for (int i = 0; i < quantidade; i++) {
            contexto.servico.inscrever(contexto.sorteio.getId(), UUID.randomUUID());
        }
    }

    @Quando("o gestor apurar o sorteio")
    public void oGestorApurarOSorteio() {
        contexto.servico.apurar(contexto.sorteio.getId());
    }

    @Então("o sorteio deve estar com status {string}")
    public void oSorteioDeveEstarComStatus(String esperado) {
        assertEquals(StatusSorteio.valueOf(esperado), contexto.sorteio.getStatus());
    }

    @E("o sorteio deve ter {int} ganhadores")
    public void oSorteioDeveTerGanhadores(Integer total) {
        assertEquals(total.longValue(), contexto.sorteio.contarPorStatus(StatusInscricao.GANHADOR));
    }

    @E("o sorteio deve ter {int} ganhador")
    public void oSorteioDeveTerGanhador(Integer total) {
        assertEquals(total.longValue(), contexto.sorteio.contarPorStatus(StatusInscricao.GANHADOR));
    }

    @E("o sorteio deve ter {int} suplentes")
    public void oSorteioDeveTerSuplentes(Integer total) {
        assertEquals(total.longValue(), contexto.sorteio.contarPorStatus(StatusInscricao.SUPLENTE));
    }

    @E("o sorteio deve ter {int} desistente")
    public void oSorteioDeveTerDesistente(Integer total) {
        assertEquals(total.longValue(), contexto.sorteio.contarPorStatus(StatusInscricao.DESISTENTE));
    }

    @Dado("um sorteio apurado com {int} vaga e {int} inscritos")
    public void umSorteioApuradoComVagaEInscritos(Integer vagas, Integer inscritos) {
        contexto.evento = criarEventoAprovado();
        when(contexto.eventoRepositorio.obter(any())).thenReturn(Optional.of(contexto.evento));
        LocalDateTime dataApresentacao = contexto.evento.getDatasApresentacao().get(0);

        contexto.sorteio = contexto.servico.criar(
                UUID.randomUUID(),
                contexto.evento.getId(),
                vagas,
                dataApresentacao.minusDays(1),
                dataApresentacao
        );
        when(contexto.sorteioRepositorio.obter(any())).thenReturn(Optional.of(contexto.sorteio));

        for (int i = 0; i < inscritos; i++) {
            contexto.servico.inscrever(contexto.sorteio.getId(), UUID.randomUUID());
        }
        contexto.servico.apurar(contexto.sorteio.getId());
    }

    @Quando("um ganhador desiste do sorteio")
    public void umGanhadorDesisteDoSorteio() {
        UUID ganhadorId = contexto.sorteio.getInscricoes().stream()
                .filter(i -> i.getStatus() == StatusInscricao.GANHADOR)
                .findFirst()
                .orElseThrow()
                .getEspectadorId();
        contexto.servico.desistir(contexto.sorteio.getId(), ganhadorId);
    }

    private UUID idDe(String nome) {
        return contexto.espectadores.computeIfAbsent(nome, n -> UUID.randomUUID());
    }

    private Evento criarEventoBase() {
        LocalDateTime agora = LocalDateTime.now();
        Evento evento = new Evento(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                "Romeu e Julieta",
                "Clássico de Shakespeare",
                "Montagem do Grupo Teatral do Recife",
                new Periodo(agora.plusDays(10), agora.plusDays(40)),
                null,
                new Preco(new BigDecimal("60.00"), new BigDecimal("30.00"), null)
        );
        evento.programarApresentacao(agora.plusDays(20));
        return evento;
    }

    private Evento criarEventoAprovado() {
        Evento evento = criarEventoBase();
        evento.submeterParaAnalise();
        evento.aprovar();
        return evento;
    }
}
