package recifecultural.dominio.cupom;

import lombok.Getter;
import org.apache.commons.lang3.Validate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
public class Cupom {
    private final CupomId id;
    private final String codigo;
    private final double percentualDesconto;
    private final double valorMinimoPedido;

    private final int limiteGlobal;
    private int usosGlobais;
    private final int limitePorCpf;

    private final LocalDateTime dataInicio;
    private final LocalDateTime dataFim;
    private final String categoriaPermitida;

    private final Set<String> cpfsQueJaUsaram;

    public Cupom(CupomId id, String codigo, double percentualDesconto,
                 double valorMinimoPedido, int limiteGlobal, int limitePorCpf,
                 LocalDateTime dataInicio, LocalDateTime dataFim, String categoriaPermitida) {

        // Validações de Invariantes com Apache
        Validate.notNull(id, "Id do cupom é obrigatório.");
        Validate.notBlank(codigo, "O código textual do cupom é obrigatório.");
        Validate.isTrue(percentualDesconto > 0 && percentualDesconto <= 100, "Desconto deve ser entre 1 e 100%.");
        Validate.notNull(dataInicio, "Data de início é obrigatória.");
        Validate.notNull(dataFim, "Data de fim é obrigatória.");
        Validate.isTrue(dataFim.isAfter(dataInicio), "A data de fim deve ser posterior à de início.");
        Validate.isTrue(limiteGlobal > 0, "O limite global deve ser maior que zero.");

        this.id = id;
        this.codigo = codigo;
        this.percentualDesconto = percentualDesconto;
        this.valorMinimoPedido = valorMinimoPedido;
        this.limiteGlobal = limiteGlobal;
        this.limitePorCpf = limitePorCpf;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.categoriaPermitida = categoriaPermitida;
        this.usosGlobais = 0;
        this.cpfsQueJaUsaram = new HashSet<>();
    }

    // Comportamentos de Negócio (Encapsulados)
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
}