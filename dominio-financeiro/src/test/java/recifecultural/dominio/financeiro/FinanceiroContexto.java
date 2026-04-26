package recifecultural.dominio.financeiro;

import recifecultural.dominio.ingressos.IIngressoRepositorio;

import java.math.BigDecimal;

import static org.mockito.Mockito.mock;

public class FinanceiroContexto {

    public final OrcamentoRepositorioEmMemoria orcamentoRepositorio;
    public final DespesaRepositorioEmMemoria despesaRepositorio;
    public final IIngressoRepositorio ingressoRepositorio;
    public final DesempenhoTeatroServico servico;

    // estado compartilhado entre step definitions
    public OrcamentoId orcamentoId;
    public ResultadoRegistroDespesa resultado;
    public Periodo periodoAtual;
    public Periodo periodoAnterior;
    public Periodo periodoSobrepostoA;
    public Periodo periodoSobrepostoB;
    public IndicadoresDesempenho indicadores;
    public ComparativoPeriodos comparativo;
    public RuntimeException excecao;

    public FinanceiroContexto() {
        orcamentoRepositorio = new OrcamentoRepositorioEmMemoria();
        despesaRepositorio = new DespesaRepositorioEmMemoria();
        ingressoRepositorio = mock(IIngressoRepositorio.class);
        servico = new DesempenhoTeatroServico(orcamentoRepositorio, despesaRepositorio, ingressoRepositorio);
    }
}
