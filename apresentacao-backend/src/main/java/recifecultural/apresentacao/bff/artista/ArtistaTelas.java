package recifecultural.apresentacao.bff.artista;

import java.util.List;
import java.util.UUID;

public class ArtistaTelas {
    public record CadastrarArtistaRequisicao(UUID produtorId, String nome, List<String> riderItens) {}
}
