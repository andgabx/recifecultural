package recifecultural.apresentacao.bff.espaco;

import java.util.List;
import java.util.UUID;

public class EspacoTelas {
    public record CadastrarEspacoRequisicao(String nome, int capacidadeMaxima, List<String> riderTecnico) {}
    public record AtualizarCapacidadeRequisicao(int novaCapacidade) {}
}
