package recifecultural.dominio.patrocinio;

public class PatrocinioContexto {

    public final PatrocinioRepositorioEmMemoria repositorio;
    public final PatrocinioServico servico;

    public PatrocinioId patrocinioId;
    public Patrocinio patrocinio;
    public ResultadoCancelamento resultadoCancelamento;
    public ResultadoSubsidio resultadoSubsidio;
    public EventoId eventoId;
    public RuntimeException excecao;

    public PatrocinioContexto() {
        repositorio = new PatrocinioRepositorioEmMemoria();
        servico = new PatrocinioServico(repositorio);
    }
}
