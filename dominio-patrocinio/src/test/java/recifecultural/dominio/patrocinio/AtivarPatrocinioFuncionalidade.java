package recifecultural.dominio.patrocinio;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AtivarPatrocinioFuncionalidade {

    private final PatrocinioContexto ctx;

    public AtivarPatrocinioFuncionalidade(PatrocinioContexto ctx) {
        this.ctx = ctx;
    }

    @Given("que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em PROPOSTA com valor {bigdecimal}")
    public void que_existe_patrocinio_em_proposta(BigDecimal valor) {
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Teste", "Cultura",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL,
                valor, LocalDateTime.now().plusDays(30), true);
        ctx.patrocinioId = patrocinio.getId();
    }

    @Given("que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em ATIVO com valor {bigdecimal}")
    public void que_existe_patrocinio_em_ativo(BigDecimal valor) {
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Teste", "Cultura",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL,
                valor, LocalDateTime.now().plusDays(30), true);
        ctx.patrocinioId = patrocinio.getId();
        ctx.servico.ativar(ctx.patrocinioId);
    }

    @Given("que existe um patrocínio de SUBSIDIO_INGRESSO_SOCIAL em ENCERRADO com valor {bigdecimal}")
    public void que_existe_patrocinio_em_encerrado(BigDecimal valor) {
        EventoId eventoId = EventoId.novo();
        Patrocinio patrocinio = ctx.servico.criar(
                eventoId, "Patrocinador Teste", "Cultura",
                TipoPatrocinio.MASTER, ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL,
                valor, LocalDateTime.now().plusDays(30), true);
        ctx.patrocinioId = patrocinio.getId();
        ctx.servico.ativar(ctx.patrocinioId);
        ctx.servico.encerrar(ctx.patrocinioId);
    }

    @When("ativo o patrocínio")
    public void ativo_o_patrocinio() {
        try {
            ctx.servico.ativar(ctx.patrocinioId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento ativar o patrocínio novamente")
    public void tento_ativar_o_patrocinio_novamente() {
        try {
            ctx.servico.ativar(ctx.patrocinioId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("encerro o patrocínio")
    public void encerro_o_patrocinio() {
        try {
            ctx.servico.encerrar(ctx.patrocinioId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }

    @When("tento encerrar o patrocínio")
    public void tento_encerrar_o_patrocinio() {
        try {
            ctx.servico.encerrar(ctx.patrocinioId);
        } catch (RuntimeException e) {
            ctx.excecao = e;
        }
    }
}
