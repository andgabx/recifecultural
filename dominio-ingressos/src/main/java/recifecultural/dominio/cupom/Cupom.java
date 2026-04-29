package recifecultural.dominio.cupom;

import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Cupom {
    private final CupomId id;
    private final String codigo;
    private final TipoDesconto tipoDesconto;
    private final double valorDesconto;
    private final double valorMinimoPedido;

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
            double valorDesconto,
            double valorMinimoPedido,
            int limiteGlobal,
            int limitePorCpf,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            String categoriaPermitida) {

        Validate.notNull(id, "Id do cupom é obrigatório.");
        Validate.notBlank(codigo, "O código textual do cupom é obrigatório.");
        Validate.notNull(tipoDesconto, "O tipo de desconto é obrigatório.");
        if (tipoDesconto == TipoDesconto.PERCENTUAL) {
            Validate.isTrue(valorDesconto > 0 && valorDesconto <= 100, "Desconto percentual deve ser entre 1 e 100%.");
        } else {
            Validate.isTrue(valorDesconto > 0, "O valor de desconto fixo deve ser maior que zero.");
        }

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

    public double calcularDesconto(double valorOriginal) {
        if (this.tipoDesconto == TipoDesconto.PERCENTUAL) {
            return valorOriginal * (this.valorDesconto / 100.0);
        } else {
            return Math.min(this.valorDesconto, valorOriginal);
        }
    }

    public void validarElegibilidade(String cpfUsuario, double valorPedido, String categoriaEvento, LocalDateTime dataAtual) {
        Validate.isTrue(dataAtual.isAfter(dataInicio) && dataAtual.isBefore(dataFim),
                "Cupom expirado ou ainda não iniciado.");

        Validate.isTrue(usosGlobais < limiteGlobal, "Limite global atingido.");

        Validate.isTrue(valorPedido >= valorMinimoPedido,
                "Pedido abaixo do valor mínimo de R$ " + valorMinimoPedido);

        if (categoriaPermitida != null) {
            Validate.isTrue(categoriaPermitida.equalsIgnoreCase(categoriaEvento),
                    "Cupom inválido para a categoria " + categoriaEvento);
        }

        long usosAtuaisCpf = cpfsQueJaUsaram.stream().filter(c -> c.equals(cpfUsuario)).count();
        Validate.isTrue(usosAtuaisCpf < limitePorCpf, "Limite por CPF atingido.");
    }

    public void registrarUso(String cpfUsuario) {
        this.usosGlobais++;
        this.cpfsQueJaUsaram.add(cpfUsuario);
    }
    public double getPercentualDesconto() {
        return valorDesconto;
    }
}
