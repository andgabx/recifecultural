package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.ingressos.MetodoPagamento;

/*
 * Padrão Strategy: define a interface comum para diferentes estratégias de
 * processamento de reembolso. Cada método de pagamento pode ter prazo
 * e comportamento distintos, selecionados em tempo de execução.
 */
public interface EstrategiaProcessamentoReembolso {
    String prazoProcessamento();
    String descricao();
    boolean aplicavelA(MetodoPagamento metodo);
}
