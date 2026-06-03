package recifecultural.dominio.cupom;

import recifecultural.dominio.cupom.validacoes.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AplicarCupomServico {
    private final ICupomRepositorio repositorio;
    private final ValidadorCupom validadorPipeline;

    public AplicarCupomServico(ICupomRepositorio repositorio) {
        this.repositorio = repositorio;

        // Padrao decorator, A validação entra pelas camadas de fora e vai até o núcleo.
        this.validadorPipeline =
                new ValidarVigenciaDecorator(
                        new ValidarMinimoDecorator(
                                new ValidarCategoriaDecorator(
                                        new ValidarEscassezGlobalDecorator(
                                                new ValidarLimiteCpfDecorator(
                                                        new ValidadorCupomBase()
                                                )
                                        )
                                )
                        )
                );
    }

    public BigDecimal aplicarDesconto(String codigo, String cpf, BigDecimal valor, String categoria) {
        Cupom cupom = buscarOuLancar(codigo);

        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now(), validadorPipeline);

        BigDecimal desconto = cupom.calcularDesconto(valor);
        cupom.registrarUso(cpf);
        repositorio.salvar(cupom);
        return valor.subtract(desconto);
    }

    public PreviewDesconto previewDesconto(String codigo, String cpf, BigDecimal valor, String categoria) {
        Cupom cupom = buscarOuLancar(codigo);

        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now(), validadorPipeline);

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