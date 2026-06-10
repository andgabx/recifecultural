package recifecultural.dominio.ingressos;

public interface EstrategiaProcessamentoReembolso {
    String prazoProcessamento();
    String descricao();
    boolean aplicavelA(MetodoPagamento metodo);
}
