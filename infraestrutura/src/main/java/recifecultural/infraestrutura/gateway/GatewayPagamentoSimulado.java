package recifecultural.infraestrutura.gateway;

import org.springframework.stereotype.Component;

import recifecultural.dominio.ingressos.IGatewayPagamento;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.ResultadoPagamento;
import recifecultural.dominio.ingressos.ResultadoReembolso;

import java.math.BigDecimal;
import java.util.UUID;

@Component
public class GatewayPagamentoSimulado implements IGatewayPagamento {

    @Override
    public ResultadoPagamento processar(IngressoId id, BigDecimal valor, MetodoPagamento metodo) {
        String codigoTransacao = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new ResultadoPagamento(codigoTransacao, true);
    }

    @Override
    public ResultadoReembolso reembolsar(String codigoTransacao, BigDecimal valor, MetodoPagamento metodo) {
        String prazo = metodo == MetodoPagamento.PIX ? "Imediato" : "Até 2 dias úteis";
        return new ResultadoReembolso(valor, true, prazo);
    }
}
