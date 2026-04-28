package recifecultural.dominio.catraca;

public interface ICatracaRepositorio {
    IngressoCatraca buscarPorId(String idIngresso);
    void salvar(IngressoCatraca ingresso);
}