package recifecultural.dominio.financeiro;

import recifecultural.dominio.ingressos.IIngressoRepositorio;
import recifecultural.dominio.ingressos.Ingresso;
import recifecultural.dominio.ingressos.StatusIngresso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class DesempenhoTeatroServico {

    private static final BigDecimal ALERTA_PERCENTUAL = new BigDecimal("0.80");

    private final IOrcamentoRepositorio orcamentoRepositorio;
    private final IDespesaRepositorio despesaRepositorio;
    private final IIngressoRepositorio ingressoRepositorio;

    public DesempenhoTeatroServico(IOrcamentoRepositorio orcamentoRepositorio,
                                    IDespesaRepositorio despesaRepositorio,
                                    IIngressoRepositorio ingressoRepositorio) {
        notNull(orcamentoRepositorio, "O repositório de orçamentos não pode ser nulo.");
        notNull(despesaRepositorio, "O repositório de despesas não pode ser nulo.");
        notNull(ingressoRepositorio, "O repositório de ingressos não pode ser nulo.");
        this.orcamentoRepositorio = orcamentoRepositorio;
        this.despesaRepositorio = despesaRepositorio;
        this.ingressoRepositorio = ingressoRepositorio;
    }

    public ResultadoRegistroDespesa registrarDespesa(OrcamentoId id,
                                                      String descricao,
                                                      BigDecimal valor,
                                                      CategoriaDespesa categoria) {
        notNull(id, "O id do orçamento não pode ser nulo.");
        notBlank(descricao, "A descrição não pode ser vazia.");
        notNull(valor, "O valor não pode ser nulo.");
        notNull(categoria, "A categoria não pode ser nula.");

        OrcamentoPeriodo orcamento = orcamentoRepositorio.buscarPorId(id);
        notNull(orcamento, "Orçamento não encontrado.");

        isTrue(orcamento.getStatus() == StatusOrcamento.ABERTO, "Orçamento encerrado.");

        LocalDateTime agora = LocalDateTime.now();
        isTrue(orcamento.getPeriodo().contem(agora),
                "Despesa fora do período orçamentário.");

        BigDecimal totalAtual = despesaRepositorio.somarPorOrcamento(id);
        boolean alertaOrcamento = totalAtual.add(valor)
                .compareTo(orcamento.getValorTotal().multiply(ALERTA_PERCENTUAL)) >= 0;

        Despesa despesa = new Despesa(DespesaId.novo(), id, descricao, valor, categoria);
        despesaRepositorio.salvar(despesa);

        return new ResultadoRegistroDespesa(despesa, alertaOrcamento);
    }

    public IndicadoresDesempenho calcularIndicadores(Periodo periodo, int capacidadeTotal) {
        notNull(periodo, "O período não pode ser nulo.");

        OrcamentoPeriodo orcamento = orcamentoRepositorio.buscarPorPeriodo(periodo);

        List<Ingresso> ingressosPeriodo = ingressoRepositorio.buscarPorPeriodo(
                periodo.getDataInicio(), periodo.getDataFim());

        int ingressosVendidos = (int) ingressosPeriodo.stream()
                .filter(i -> i.getStatus() == StatusIngresso.ATIVO
                        || i.getStatus() == StatusIngresso.REEMBOLSADO
                        || i.getStatus() == StatusIngresso.UTILIZADO)
                .count();

        BigDecimal receitaBruta = ingressosPeriodo.stream()
                .map(Ingresso::getValorPago)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReembolsos = ingressosPeriodo.stream()
                .filter(i -> i.getStatus() == StatusIngresso.REEMBOLSADO)
                .map(i -> i.getValorReembolsado() != null ? i.getValorReembolsado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = orcamento != null
                ? despesaRepositorio.somarPorOrcamento(orcamento.getId())
                : BigDecimal.ZERO;

        BigDecimal receitaLiquida = receitaBruta.subtract(totalDespesas).subtract(totalReembolsos);

        BigDecimal taxaOcupacao = capacidadeTotal > 0
                ? new BigDecimal(ingressosVendidos)
                .divide(new BigDecimal(capacidadeTotal), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new IndicadoresDesempenho(taxaOcupacao, receitaBruta, receitaLiquida, totalDespesas, null);
    }

    public ComparativoPeriodos compararPeriodos(Periodo anterior, Periodo atual, int capacidadeTotal) {
        notNull(anterior, "O período anterior não pode ser nulo.");
        notNull(atual, "O período atual não pode ser nulo.");
        isTrue(!anterior.sobrepoe(atual), "Períodos não podem se sobrepor.");

        IndicadoresDesempenho indicadoresAnteriores = calcularIndicadores(anterior, capacidadeTotal);
        IndicadoresDesempenho indicadoresAtuais = calcularIndicadores(atual, capacidadeTotal);

        return new ComparativoPeriodos(indicadoresAnteriores, indicadoresAtuais);
    }
}
