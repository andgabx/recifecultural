package recifecultural.dominio.ingressos;

import java.math.BigDecimal;

public interface IGatewayPagamento {

    ResultadoPagamento processar(IngressoId id, BigDecimal valor, MetodoPagamento metodo);

    ResultadoReembolso reembolsar(String codigoTransacao, BigDecimal valor, MetodoPagamento metodo);
}
