package recifecultural.aplicacao.artista.artista;

import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.List;

public interface ArtistaRepositorioAplicacao {
    List<ArtistaResumo> pesquisarResumos();
    List<ArtistaResumo> pesquisarResumosPorProdutor(ProdutorId produtorId);
}
