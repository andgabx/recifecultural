package recifecultural.dominio.cupom;

import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@AllArgsConstructor
public class AplicarCupomServico {
    private final ICupomRepositorio repositorio;

    public double aplicarDesconto(String codigo, String cpf, double valor, String categoria) {
        Cupom cupom = repositorio.buscarPorCodigo(codigo);


        cupom.validarElegibilidade(cpf, valor, categoria, LocalDateTime.now());

        double valorDesconto = valor * (cupom.getPercentualDesconto() / 100);

        cupom.registrarUso(cpf);
        repositorio.salvar(cupom);

        return valor - valorDesconto;
    }
}