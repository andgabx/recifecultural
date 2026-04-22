package recifecultural.dominio.artistas;

public class ArtistaFuncionalidade {

    protected ArtistaRepositorioEmMemoria repositorio;
    protected ArtistaServico servico;

    public ArtistaFuncionalidade() {
        repositorio = new ArtistaRepositorioEmMemoria();
        servico = new ArtistaServico(repositorio);
    }
}
