package recifecultural.dominio.cupom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AplicarCupomServico {
    private final ICupomRepositorio repositorio;

    public AplicarCupomServico(ICupomRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /** Aplica desconto, registra uso e persiste. Usar apenas na finalização da compra. */
    public BigDecimal aplicarDesconto(String codigo, String cpf, BigDecimal valor, String categoria) {
        Cupom cupom = buscarOuLancar(codigo);
        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now());
        BigDecimal desconto = cupom.calcularDesconto(valor);
        cupom.registrarUso(cpf);
        repositorio.salvar(cupom);
        return valor.subtract(desconto);
    }

    /** Valida e calcula o desconto SEM registrar uso — usar para preview antes de finalizar. */
    public PreviewDesconto previewDesconto(String codigo, String cpf, BigDecimal valor, String categoria) {
        Cupom cupom = buscarOuLancar(codigo);
        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now());
        BigDecimal desconto = cupom.calcularDesconto(valor);
        return new PreviewDesconto(
                cupom.getTipoDesconto().name(),
                cupom.getValorDesconto(),
                desconto,
                valor.subtract(desconto));
    }

    private Cupom buscarOuLancar(String codigo) {
        Cupom cupom = repositorio.buscarPorCodigo(codigo);
        if (cupom == null) throw new IllegalArgumentException("Cupom não encontrado: " + codigo);
        return cupom;
    }

    public record PreviewDesconto(
            String tipoDesconto,
            BigDecimal configuracaoDesconto,
            BigDecimal descontoCalculado,
            BigDecimal valorFinal) {}
}
