package recifecultural.dominio.cupom;

import java.time.LocalDateTime;

public class AplicarCupomServico {
    private final ICupomRepositorio repositorio;

    public AplicarCupomServico(ICupomRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public double aplicarDesconto(String codigo, String cpf, double valor, String categoria) {
        Cupom cupom = repositorio.buscarPorCodigo(codigo);

        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now());

        double valorDesconto = cupom.calcularDesconto(valor);

        cupom.registrarUso(cpf);
        repositorio.salvar(cupom);

        return valor - valorDesconto;
    }
}
