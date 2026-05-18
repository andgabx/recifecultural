package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.ingressos.IGatewayPagamento;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.ResultadoPagamento;
import recifecultural.dominio.ingressos.ResultadoReembolso;

import java.math.BigDecimal;

/*
 * Padrão Decorator: decorador abstrato para IGatewayPagamento. Permite adicionar
 * comportamentos (log, retry, métricas) dinamicamente sem alterar o gateway original.
 */
public abstract class GatewayPagamentoDecorador implements IGatewayPagamento {

    protected final IGatewayPagamento delegado;

    protected GatewayPagamentoDecorador(IGatewayPagamento delegado) {
        if (delegado == null) throw new IllegalArgumentException("Delegado não pode ser nulo.");
        this.delegado = delegado;
    }

    @Override
    public ResultadoPagamento processar(IngressoId id, BigDecimal valor, MetodoPagamento metodo) {
        return delegado.processar(id, valor, metodo);
    }

    @Override
    public ResultadoReembolso reembolsar(String codigoTransacao, BigDecimal valor, MetodoPagamento metodo) {
        return delegado.reembolsar(codigoTransacao, valor, metodo);
    }
}
