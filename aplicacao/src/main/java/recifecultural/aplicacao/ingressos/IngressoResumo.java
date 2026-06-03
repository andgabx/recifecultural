package recifecultural.aplicacao.ingressos;

public interface IngressoResumo {
    String getId();
    String getEventoId();
    String getTipo();
    String getStatus();
    String getDataHoraApresentacao();
    String getDataCompra();
    String getMetodoPagamento();
    String getValorPago();
    String getCodigoQr();
}
