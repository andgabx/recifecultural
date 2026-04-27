package recifecultural.dominio.artista.artista;

import recifecultural.dominio.artista.produtor.ProdutorId;

public class Artista {

    private final ArtistaId id;
    private final ProdutorId produtorId;
    private String nome;
    private RiderTecnico riderTecnico;
    private StatusArtista status;

    public Artista(ProdutorId produtorId, String nome, RiderTecnico riderTecnico) {
        if (produtorId == null) throw new IllegalArgumentException("Produtor é obrigatório.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");

        this.id = ArtistaId.novo();
        this.produtorId = produtorId;
        this.nome = nome;
        this.riderTecnico = riderTecnico != null ? riderTecnico : RiderTecnico.vazio();
        this.status = StatusArtista.ATIVO;
    }

    public Artista(ArtistaId id, ProdutorId produtorId, String nome, RiderTecnico riderTecnico, StatusArtista status) {
        if (id == null) throw new IllegalArgumentException("ID é obrigatório.");
        this.id = id;
        this.produtorId = produtorId;
        this.nome = nome;
        this.riderTecnico = riderTecnico;
        this.status = status;
    }

    public void atualizarRider(RiderTecnico novoRider) {
        if (this.status == StatusArtista.INATIVO)
            throw new IllegalStateException("Não é possível atualizar rider de artista inativo.");
        if (novoRider == null) throw new IllegalArgumentException("Rider não pode ser nulo.");
        this.riderTecnico = novoRider;
    }

    public void inativar() {
        if (this.status == StatusArtista.INATIVO)
            throw new IllegalStateException("Artista já está inativo.");
        this.status = StatusArtista.INATIVO;
    }

    public ArtistaId getId() { return id; }
    public ProdutorId getProdutorId() { return produtorId; }
    public String getNome() { return nome; }
    public RiderTecnico getRiderTecnico() { return riderTecnico; }
    public StatusArtista getStatus() { return status; }
}