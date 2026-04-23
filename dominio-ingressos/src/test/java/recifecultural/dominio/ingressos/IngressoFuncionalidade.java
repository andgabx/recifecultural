package recifecultural.dominio.ingressos;

public class IngressoFuncionalidade {

    protected IngressoRepositorioEmMemoria repositorio;
    protected IGatewayPagamento gateway;
    protected IngressoServico servico;

    public IngressoFuncionalidade() {
        repositorio = new IngressoRepositorioEmMemoria();
        gateway = new GatewayPagamentoMock();
        servico = new IngressoServico(repositorio, gateway);
    }
}
