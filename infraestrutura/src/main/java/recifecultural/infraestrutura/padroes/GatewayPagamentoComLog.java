package recifecultural.infraestrutura.padroes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import recifecultural.dominio.ingressos.IGatewayPagamento;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.ResultadoPagamento;
import recifecultural.dominio.ingressos.ResultadoReembolso;

import java.math.BigDecimal;

public class GatewayPagamentoComLog extends GatewayPagamentoDecorador {

    private static final Logger log = LoggerFactory.getLogger(GatewayPagamentoComLog.class);

    public GatewayPagamentoComLog(IGatewayPagamento delegado) {
        super(delegado);
    }

    @Override
    public ResultadoPagamento processar(IngressoId id, BigDecimal valor, MetodoPagamento metodo) {
        log.info("[GATEWAY] Processando pagamento ingresso={} valor={} metodo={}", id, valor, metodo);
        var resultado = super.processar(id, valor, metodo);
        log.info("[GATEWAY] Resultado pagamento aprovado={} transacao={}", resultado.isAprovado(), resultado.getCodigoTransacao());
        return resultado;
    }

    @Override
    public ResultadoReembolso reembolsar(String codigoTransacao, BigDecimal valor, MetodoPagamento metodo) {
        log.info("[GATEWAY] Reembolsando transacao={} valor={}", codigoTransacao, valor);
        var resultado = super.reembolsar(codigoTransacao, valor, metodo);
        log.info("[GATEWAY] Reembolso processado valor={}", resultado.getValorReembolsado());
        return resultado;
    }
}
