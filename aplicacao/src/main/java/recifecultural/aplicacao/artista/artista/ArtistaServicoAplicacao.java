package recifecultural.aplicacao.artista.artista;

import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.artista.ArtistaServico;
import recifecultural.dominio.artista.artista.ItemRider;
import recifecultural.dominio.artista.artista.RiderTecnico;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.notNull;

public class ArtistaServicoAplicacao {

    private final ArtistaServico servico;
    private final ArtistaRepositorioAplicacao repositorio;

    public ArtistaServicoAplicacao(ArtistaServico servico, ArtistaRepositorioAplicacao repositorio) {
        notNull(servico, "ArtistaServico não pode ser nulo.");
        notNull(repositorio, "ArtistaRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<ArtistaResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    public List<ArtistaResumo> pesquisarResumosPorProdutor(ProdutorId produtorId) {
        notNull(produtorId, "ProdutorId não pode ser nulo.");
        return repositorio.pesquisarResumosPorProdutor(produtorId);
    }

    public ArtistaId cadastrar(ProdutorId produtorId, String nome, RiderTecnico riderTecnico) {
        return servico.cadastrar(produtorId, nome, riderTecnico);
    }

    public void inativar(ArtistaId artistaId) {
        servico.inativar(artistaId);
    }

    public void reativar(ArtistaId artistaId) {
        servico.reativar(artistaId);
    }

    public static RiderTecnico construirRider(List<String> itens) {
        if (itens == null || itens.isEmpty()) return null;
        Set<ItemRider> set = itens.stream().map(ItemRider::valueOf).collect(Collectors.toSet());
        return new RiderTecnico(set);
    }
}
