package recifecultural.apresentacao.bff.produtor;

public class ProdutorTelas {
    public record CadastrarProdutorRequisicao(String nomeFantasia, String cnpj, String email, String telefone) {}
    public record AcaoAdministrativaRequisicao(String responsavel, String motivo) {}
}
