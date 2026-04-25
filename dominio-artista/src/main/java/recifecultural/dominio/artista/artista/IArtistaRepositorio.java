package recifecultural.dominio.artista.artista;

import recifecultural.dominio.artista.produtor.ProdutorId;
import java.util.List;
import java.util.Optional;

public interface IArtistaRepositorio {
    void salvar(Artista artista);
    void atualizar(Artista artista);
    Optional<Artista> obterPorId(ArtistaId id);
    List<Artista> listarPorProdutor(ProdutorId produtorId);
    boolean existePorNomeEProdutor(String nome, ProdutorId produtorId);
}