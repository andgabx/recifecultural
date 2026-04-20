package recifecultural.dominio.artistas;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Artista {

    private final ArtistId id;
    private final LocalDateTime dataSubmissao;

    private String nome;
    private String bio;
    private String email;
    private String telefone;
    private final List<RedeSocial> redesSociais;

    private StatusArtista status;
    private String motivoRejeicao;
    private UUID aprovadoPorId;

    public Artista(String nome, String bio, String email, String telefone) {
        this.id = ArtistId.novo();
        this.dataSubmissao = LocalDateTime.now();
        this.redesSociais = new ArrayList<>();
        this.status = StatusArtista.PENDENTE;

        setNome(nome);
        setBio(bio);
        setEmail(email);
        this.telefone = telefone;
    }

    public void aprovar(UUID promotorId) {
        notNull(promotorId, "Promotor é obrigatório para aprovar.");
        isTrue(status == StatusArtista.PENDENTE, "Apenas artistas com status PENDENTE podem ser aprovados.");
        this.status = StatusArtista.APROVADO;
        this.aprovadoPorId = promotorId;
        this.motivoRejeicao = null;
    }

    public void rejeitar(UUID promotorId, String motivo) {
        notNull(promotorId, "Promotor é obrigatório para rejeitar.");
        isTrue(status == StatusArtista.PENDENTE, "Apenas artistas com status PENDENTE podem ser rejeitados.");
        notBlank(motivo, "Motivo de rejeição é obrigatório.");
        this.status = StatusArtista.REJEITADO;
        this.motivoRejeicao = motivo;
        this.aprovadoPorId = promotorId;
    }

    public void resubmeter() {
        isTrue(status == StatusArtista.REJEITADO, "Apenas artistas com status REJEITADO podem resubmeter o cadastro.");
        this.status = StatusArtista.PENDENTE;
        this.motivoRejeicao = null;
        this.aprovadoPorId = null;
    }

    public ArtistId getId() { return id; }

    public LocalDateTime getDataSubmissao() { return dataSubmissao; }

    public String getNome() { return nome; }

    public String getBio() { return bio; }

    public String getEmail() { return email; }

    public String getTelefone() { return telefone; }

    public StatusArtista getStatus() { return status; }

    public String getMotivoRejeicao() { return motivoRejeicao; }

    public UUID getAprovadoPorId() { return aprovadoPorId; }

    public void adicionarRedeSocial(RedeSocial redeSocial) {
        notNull(redeSocial, "Rede social não pode ser nula.");
        this.redesSociais.add(redeSocial);
    }

    public List<RedeSocial> getRedesSociais() {
        return Collections.unmodifiableList(redesSociais);
    }

    private void setNome(String nome) {
        notBlank(nome, "Nome do artista é obrigatório.");
        this.nome = nome;
    }

    private void setBio(String bio) {
        notBlank(bio, "Bio do artista é obrigatória.");
        this.bio = bio;
    }

    private void setEmail(String email) {
        notBlank(email, "E-mail do artista é obrigatório.");
        isTrue(email.contains("@"), "E-mail do artista é inválido.");
        this.email = email;
    }
}
