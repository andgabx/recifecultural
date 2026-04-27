package recifecultural.dominio.artista.produtor;

import java.util.Optional;

public interface IProdutorRepositorio {
    void salvar(Produtor produtor);
    void atualizar(Produtor produtor);
    Optional<Produtor> obterPorId(ProdutorId id);
    boolean existePorCnpj(Cnpj cnpj);
}