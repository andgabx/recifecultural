package recifecultural.dominio.ingressos;

import java.math.BigDecimal;
import java.util.UUID;

public class GatewayPagamentoMock implements IGatewayPagamento {

    @Override
    public ResultadoPagamento processar(IngressoId id, BigDecimal valor, MetodoPagamento metodo) {
        return new ResultadoPagamento(UUID.randomUUID().toString(), true);
    }

    @Override
    public ResultadoReembolso reembolsar(String codigoTransacao, BigDecimal valor, MetodoPagamento metodo) {
        String prazo = metodo == MetodoPagamento.PIX ? "imediato" : "até 2 dias úteis";
        return new ResultadoReembolso(valor, true, prazo);
    }
}
