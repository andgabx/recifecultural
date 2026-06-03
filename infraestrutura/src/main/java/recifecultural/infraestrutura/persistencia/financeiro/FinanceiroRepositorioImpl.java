package recifecultural.infraestrutura.persistencia.financeiro;

import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.financeiro.FinanceiroRepositorioAplicacao;
import recifecultural.aplicacao.financeiro.IndicadoresResumo;
import recifecultural.infraestrutura.persistencia.ingressos.IngressoJpaRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Repository
public class FinanceiroRepositorioImpl implements FinanceiroRepositorioAplicacao {

    private final IngressoJpaRepository ingressoJpa;
    private final OrcamentoPeriodoJpaRepository orcamentoJpa;
    private final DespesaJpaRepository despesaJpa;

    public FinanceiroRepositorioImpl(IngressoJpaRepository ingressoJpa,
                                     OrcamentoPeriodoJpaRepository orcamentoJpa,
                                     DespesaJpaRepository despesaJpa) {
        this.ingressoJpa = ingressoJpa;
        this.orcamentoJpa = orcamentoJpa;
        this.despesaJpa = despesaJpa;
    }

    @Override
    public IndicadoresResumo buscarIndicadores(LocalDate inicio, LocalDate fim, int capacidadeTotal) {
        LocalDateTime dtInicio = inicio.atStartOfDay();
        LocalDateTime dtFim = fim.atTime(23, 59, 59);

        var ingressos = ingressoJpa.findByPeriodo(dtInicio, dtFim);

        BigDecimal receitaBruta = ingressos.stream()
                .map(i -> i.getValorPago() != null ? i.getValorPago() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReembolsos = ingressos.stream()
                .map(i -> i.getValorReembolsado() != null ? i.getValorReembolsado() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var orcamento = orcamentoJpa.findByPeriodo(dtInicio, dtFim);
        BigDecimal totalDespesas = orcamento != null
                ? despesaJpa.somarPorOrcamento(orcamento.id)
                : BigDecimal.ZERO;
        if (totalDespesas == null) totalDespesas = BigDecimal.ZERO;

        BigDecimal receitaLiquida = receitaBruta.subtract(totalDespesas).subtract(totalReembolsos);

        int vendidos = ingressos.size();
        BigDecimal taxaOcupacao = capacidadeTotal > 0
                ? new BigDecimal(vendidos).divide(new BigDecimal(capacidadeTotal), 4, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        final BigDecimal tOcupacao = taxaOcupacao;
        final BigDecimal rBruta = receitaBruta;
        final BigDecimal rLiquida = receitaLiquida;
        final BigDecimal tDespesas = totalDespesas;

        return new IndicadoresResumo() {
            public BigDecimal getTaxaOcupacao() { return tOcupacao; }
            public BigDecimal getReceitaBruta() { return rBruta; }
            public BigDecimal getReceitaLiquida() { return rLiquida; }
            public BigDecimal getTotalDespesas() { return tDespesas; }
        };
    }
}
