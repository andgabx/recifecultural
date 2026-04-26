package recifecultural.dominio.cupom;

public interface ICupomRepositorio {
    Cupom buscarPorCodigo(String codigo);
    void salvar(Cupom cupom);

}