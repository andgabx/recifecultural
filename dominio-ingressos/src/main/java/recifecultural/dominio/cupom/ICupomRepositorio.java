package recifecultural.dominio.cupom;

import java.util.List;

public interface ICupomRepositorio {
    Cupom buscarPorCodigo(String codigo);
    Cupom buscarPorId(CupomId id);
    List<Cupom> listarTodos();
    void salvar(Cupom cupom);
    void deletar(CupomId id);
}
