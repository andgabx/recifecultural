package recifecultural.dominio.cupom;

import recifecultural.dominio.cupom.validacoes.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class AplicarCupomServico {
    private final ICupomRepositorio repositorio;
    private final List<ValidacaoCupomStrategy> validacoes; // O Pipeline!

    public AplicarCupomServico(ICupomRepositorio repositorio) {
        this.repositorio = repositorio;
        this.validacoes = List.of(
                new ValidarVigenciaStrategy(),
                new ValidarMinimoStrategy(),
                new ValidarCategoriaStrategy(),
                new ValidarEscassezGlobalStrategy(),
                new ValidarLimiteCpfStrategy()
        );
    }

    public BigDecimal aplicarDesconto(String codigo, String cpf, BigDecimal valor, String categoria) {
        Cupom cupom = buscarOuLancar(codigo);

        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now(), validacoes);

        BigDecimal desconto = cupom.calcularDesconto(valor);
        cupom.registrarUso(cpf);
        repositorio.salvar(cupom);
        return valor.subtract(desconto);
    }

    public PreviewDesconto previewDesconto(String codigo, String cpf, BigDecimal valor, String categoria) {
        Cupom cupom = buscarOuLancar(codigo);

        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now(), validacoes);

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