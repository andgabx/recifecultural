package recifecultural.dominio.cupom;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AplicarCupomServico {
    private final ICupomRepositorio repositorio;

    public AplicarCupomServico(ICupomRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public BigDecimal aplicarDesconto(String codigo, String cpf, BigDecimal valor, String categoria) {
        Cupom cupom = repositorio.buscarPorCodigo(codigo);

        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now());

        BigDecimal valorDesconto = cupom.calcularDesconto(valor);

        cupom.registrarUso(cpf);
        repositorio.salvar(cupom);

        return valor.subtract(valorDesconto);
    }
}
