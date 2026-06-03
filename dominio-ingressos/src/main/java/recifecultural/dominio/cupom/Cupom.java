package recifecultural.dominio.cupom;

import org.apache.commons.lang3.Validate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import recifecultural.dominio.cupom.validacoes.ValidadorCupom;

public class Cupom {

    private static final BigDecimal CEM = new BigDecimal("100");

    private final CupomId id;
    private final String codigo;
    private final TipoDesconto tipoDesconto;
    private final BigDecimal valorDesconto;
    private final BigDecimal valorMinimoPedido;

    private final int limiteGlobal;
    private int usosGlobais;
    private final int limitePorCpf;

    private final LocalDateTime dataInicio;
    private final LocalDateTime dataFim;
    private final String categoriaPermitida;

    private final Set<String> cpfsQueJaUsaram;

    public Cupom(
            CupomId id,
            String codigo,
            TipoDesconto tipoDesconto,
            BigDecimal valorDesconto,
            BigDecimal valorMinimoPedido,
            int limiteGlobal,
            int limitePorCpf,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String categoriaPermitida) {

        Validate.notNull(id, "Id do cupom é obrigatório.");
        Validate.notBlank(codigo, "O código textual do cupom é obrigatório.");
        Validate.notNull(tipoDesconto, "O tipo de desconto é obrigatório.");
        Validate.notNull(valorDesconto, "O valor de desconto é obrigatório.");
        Validate.notNull(valorMinimoPedido, "O valor mínimo do pedido é obrigatório.");

        if (tipoDesconto == TipoDesconto.PERCENTUAL) {
            Validate.isTrue(
                    valorDesconto.compareTo(BigDecimal.ZERO) > 0 && valorDesconto.compareTo(CEM) <= 0,
                    "Desconto percentual deve ser entre 1 e 100%.");
        } else {
            Validate.isTrue(valorDesconto.compareTo(BigDecimal.ZERO) > 0,
                    "O valor de desconto fixo deve ser maior que zero.");
        }

        Validate.isTrue(valorMinimoPedido.compareTo(BigDecimal.ZERO) >= 0,
                "Valor mínimo de pedido não pode ser negativo.");

        Validate.notNull(dataInicio, "Data de início é obrigatória.");
        Validate.notNull(dataFim, "Data de fim é obrigatória.");
        Validate.isTrue(dataFim.isAfter(dataInicio), "A data de fim deve ser posterior à de início.");
        Validate.isTrue(limiteGlobal > 0, "O limite global deve ser maior que zero.");

        this.id = id;
        this.codigo = codigo;
        this.tipoDesconto = tipoDesconto;
        this.valorDesconto = valorDesconto;
        this.valorMinimoPedido = valorMinimoPedido;
        this.limiteGlobal = limiteGlobal;
        this.limitePorCpf = limitePorCpf;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.categoriaPermitida = categoriaPermitida;
        this.usosGlobais = 0;
        this.cpfsQueJaUsaram = new HashSet<>();
    }

    public BigDecimal calcularDesconto(BigDecimal valorOriginal) {
        Validate.notNull(valorOriginal, "Valor original é obrigatório.");
        if (this.tipoDesconto == TipoDesconto.PERCENTUAL) {
            return valorOriginal.multiply(this.valorDesconto)
                    .divide(CEM, 2, RoundingMode.HALF_UP);
        }
        return this.valorDesconto.min(valorOriginal);
    }

    // =======================================================
    // PADRÃO DECORATOR EM AÇÃO: Passa o cupom pelo pipeline
    // =======================================================
    public void validarElegibilidade(String cpfUsuario, BigDecimal valorPedido, String categoriaEvento,
                                     LocalDateTime dataAtual, ValidadorCupom validador) {
        Validate.notNull(valorPedido, "Valor do pedido é obrigatório.");
        validador.validar(this, cpfUsuario, valorPedido, categoriaEvento, dataAtual);
    }

    public void registrarUso(String cpfUsuario) {
        this.usosGlobais++;
        this.cpfsQueJaUsaram.add(cpfUsuario);
    }
    public CupomId getId() { return id; }
    public String getCodigo() { return codigo; }
    public TipoDesconto getTipoDesconto() { return tipoDesconto; }
    public BigDecimal getValorDesconto() { return valorDesconto; }
    public BigDecimal getValorMinimoPedido() { return valorMinimoPedido; }
    public int getLimiteGlobal() { return limiteGlobal; }
    public int getUsosGlobais() { return usosGlobais; }
    public int getLimitePorCpf() { return limitePorCpf; }
    public LocalDateTime getDataInicio() { return dataInicio; }
    public LocalDateTime getDataFim() { return dataFim; }
    public String getCategoriaPermitida() { return categoriaPermitida; }
    public Set<String> getCpfsQueJaUsaram() { return cpfsQueJaUsaram; }
}