package recifecultural.dominio.ingressos;

public class EstrategiaReembolsoBancario implements EstrategiaProcessamentoReembolso {

    @Override
    public String prazoProcessamento() {
        return "Até 2 dias úteis";
    }

    @Override
    public String descricao() {
        return "Estorno no cartão de crédito ou débito processado pela operadora em até 2 dias úteis.";
    }

    @Override
    public boolean aplicavelA(MetodoPagamento metodo) {
        return metodo == MetodoPagamento.CARTAO_CREDITO;
    }
}
