package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.ingressos.MetodoPagamento;

public class EstrategiaReembolsoImediato implements EstrategiaProcessamentoReembolso {

    @Override
    public String prazoProcessamento() {
        return "Imediato";
    }

    @Override
    public String descricao() {
        return "Estorno PIX devolvido na hora para a chave de origem.";
    }

    @Override
    public boolean aplicavelA(MetodoPagamento metodo) {
        return metodo == MetodoPagamento.PIX;
    }
}
