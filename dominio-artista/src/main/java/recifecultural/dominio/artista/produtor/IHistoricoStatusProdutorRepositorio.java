package recifecultural.dominio.artista.produtor;

import java.util.List;

public interface IHistoricoStatusProdutorRepositorio {
    void salvar(HistoricoStatusProdutor historico);
    List<HistoricoStatusProdutor> listarPorProdutor(ProdutorId produtorId);
}
