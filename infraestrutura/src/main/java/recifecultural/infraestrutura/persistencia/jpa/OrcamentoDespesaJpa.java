package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.financeiro.FinanceiroRepositorioAplicacao;
import recifecultural.aplicacao.financeiro.IndicadoresResumo;
import recifecultural.dominio.financeiro.CategoriaDespesa;
import recifecultural.dominio.financeiro.Despesa;
import recifecultural.dominio.financeiro.DespesaId;
import recifecultural.dominio.financeiro.IDespesaRepositorio;
import recifecultural.dominio.financeiro.IOrcamentoRepositorio;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.dominio.financeiro.OrcamentoPeriodo;
import recifecultural.dominio.financeiro.Periodo;
import recifecultural.dominio.financeiro.StatusOrcamento;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orcamento_periodo")
class OrcamentoPeriodoJpa {
    @Id
    UUID id;
    LocalDateTime periodoInicio;
    LocalDateTime periodoFim;
    BigDecimal valorTotal;
    @Enumerated(EnumType.STRING)
    StatusOrcamento status;
}

@Entity
@Table(name = "despesa")
class DespesaJpa {
    @Id
    UUID id;
    UUID orcamentoId;
    String descricao;
    BigDecimal valor;
    @Enumerated(EnumType.STRING)
    CategoriaDespesa categoria;
    LocalDateTime dataRegistro;
}

interface OrcamentoPeriodoJpaRepository extends JpaRepository<OrcamentoPeriodoJpa, UUID> {
    @Query("SELECT o FROM OrcamentoPeriodoJpa o WHERE o.periodoInicio <= :fim AND o.periodoFim >= :inicio")
    OrcamentoPeriodoJpa findByPeriodo(LocalDateTime inicio, LocalDateTime fim);
}

interface DespesaJpaRepository extends JpaRepository<DespesaJpa, UUID> {
    List<DespesaJpa> findByOrcamentoId(UUID orcamentoId);

    @Query("SELECT COALESCE(SUM(d.valor), 0) FROM DespesaJpa d WHERE d.orcamentoId = :orcamentoId")
    BigDecimal somarPorOrcamento(UUID orcamentoId);
}

@Repository
class OrcamentoRepositorioImpl implements IOrcamentoRepositorio {

    private final OrcamentoPeriodoJpaRepository jpa;
    private final JpaMapeador mapeador;

    OrcamentoRepositorioImpl(OrcamentoPeriodoJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(OrcamentoPeriodo orcamento) {
        jpa.save(mapeador.map(orcamento, OrcamentoPeriodoJpa.class));
    }

    @Override
    public OrcamentoPeriodo buscarPorId(OrcamentoId id) {
        return jpa.findById(id.valor()).map(o -> mapeador.map(o, OrcamentoPeriodo.class)).orElse(null);
    }

    @Override
    public OrcamentoPeriodo buscarPorPeriodo(Periodo periodo) {
        var jpaObj = jpa.findByPeriodo(periodo.getDataInicio(), periodo.getDataFim());
        return jpaObj != null ? mapeador.map(jpaObj, OrcamentoPeriodo.class) : null;
    }
}

@Repository
class DespesaRepositorioImpl implements IDespesaRepositorio {

    private final DespesaJpaRepository jpa;
    private final JpaMapeador mapeador;

    DespesaRepositorioImpl(DespesaJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Despesa despesa) {
        jpa.save(mapeador.map(despesa, DespesaJpa.class));
    }

    @Override
    public List<Despesa> buscarPorOrcamento(OrcamentoId id) {
        return jpa.findByOrcamentoId(id.valor())
                .stream().map(d -> mapeador.map(d, Despesa.class)).toList();
    }

    @Override
    public BigDecimal somarPorOrcamento(OrcamentoId id) {
        BigDecimal soma = jpa.somarPorOrcamento(id.valor());
        return soma != null ? soma : BigDecimal.ZERO;
    }
}

@Repository
class FinanceiroRepositorioImpl implements FinanceiroRepositorioAplicacao {

    private final IngressoJpaRepository ingressoJpa;
    private final OrcamentoPeriodoJpaRepository orcamentoJpa;
    private final DespesaJpaRepository despesaJpa;

    FinanceiroRepositorioImpl(IngressoJpaRepository ingressoJpa,
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
                .map(i -> i.valorPago != null ? i.valorPago : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalReembolsos = ingressos.stream()
                .map(i -> i.valorReembolsado != null ? i.valorReembolsado : BigDecimal.ZERO)
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
