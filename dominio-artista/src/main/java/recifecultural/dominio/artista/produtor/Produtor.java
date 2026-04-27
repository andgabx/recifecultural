package recifecultural.dominio.artista.produtor;

public class Produtor {

    private final ProdutorId id;
    private String nomeFantasia;
    private Cnpj cnpj;
    private String email;
    private String telefone;
    private StatusProdutor status;

    public Produtor(String nomeFantasia, Cnpj cnpj, String email, String telefone) {
        if (nomeFantasia == null || nomeFantasia.isBlank()) throw new IllegalArgumentException("Nome fantasia é obrigatório.");
        if (cnpj == null) throw new IllegalArgumentException("CNPJ é obrigatório.");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("E-mail inválido.");
        this.id = ProdutorId.novo();
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        this.status = StatusProdutor.ATIVO;
    }

    public Produtor(ProdutorId id, String nomeFantasia, Cnpj cnpj,
                    String email, String telefone, StatusProdutor status) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        this.id = id;
        this.nomeFantasia = nomeFantasia;
        this.cnpj = cnpj;
        this.email = email;
        this.telefone = telefone;
        this.status = status;
    }

    public void atualizarContato(String novoEmail, String novoTelefone) {
        if (this.status == StatusProdutor.INATIVO)
            throw new IllegalStateException("Não é possível atualizar dados de produtor inativo.");
        if (novoEmail == null || !novoEmail.contains("@"))
            throw new IllegalArgumentException("E-mail inválido.");
        this.email = novoEmail;
        this.telefone = novoTelefone;
    }

    public void suspender() {
        if (this.status == StatusProdutor.SUSPENSO) throw new IllegalStateException("Produtor já está suspenso.");
        if (this.status == StatusProdutor.INATIVO) throw new IllegalStateException("Produtor inativo não pode ser suspenso.");
        this.status = StatusProdutor.SUSPENSO;
    }

    public void reativar() {
        if (this.status == StatusProdutor.ATIVO) throw new IllegalStateException("Produtor já está ativo.");
        if (this.status == StatusProdutor.INATIVO) throw new IllegalStateException("Produtor inativo não pode ser reativado. Realize um novo cadastro.");
        this.status = StatusProdutor.ATIVO;
    }

    public void inativar() {
        if (this.status == StatusProdutor.INATIVO) throw new IllegalStateException("Produtor já está inativo.");
        this.status = StatusProdutor.INATIVO;
    }

    public void anonimizarDadosPessoais() {
        if (this.status != StatusProdutor.INATIVO)
            throw new IllegalStateException("Dados só podem ser anonimizados após inativação.");
        this.email = "anonimizado@lgpd.recife.br";
        this.telefone = null;
    }

    public ProdutorId getId() { return id; }
    public String getNomeFantasia() { return nomeFantasia; }
    public Cnpj getCnpj() { return cnpj; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public StatusProdutor getStatus() { return status; }
}