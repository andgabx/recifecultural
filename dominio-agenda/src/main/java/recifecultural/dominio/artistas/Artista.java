package recifecultural.dominio.artistas;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
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
        if (promotorId == null) throw new IllegalArgumentException("Promotor é obrigatório para aprovar.");
        if (status != StatusArtista.PENDENTE) throw new IllegalStateException("Apenas artistas com status PENDENTE podem ser aprovados.");
        this.status = StatusArtista.APROVADO;
        this.aprovadoPorId = promotorId;
        this.motivoRejeicao = null;
    }

    public void rejeitar(UUID promotorId, String motivo) {
        if (promotorId == null) throw new IllegalArgumentException("Promotor é obrigatório para rejeitar.");
        if (status != StatusArtista.PENDENTE) throw new IllegalStateException("Apenas artistas com status PENDENTE podem ser rejeitados.");
        if (motivo == null || motivo.isBlank()) throw new IllegalArgumentException("Motivo de rejeição é obrigatório.");
        this.status = StatusArtista.REJEITADO;
        this.motivoRejeicao = motivo;
        this.aprovadoPorId = promotorId;
    }

    public void resubmeter() {
        if (status != StatusArtista.REJEITADO) throw new IllegalStateException("Apenas artistas com status REJEITADO podem resubmeter o cadastro.");
        this.status = StatusArtista.PENDENTE;
        this.motivoRejeicao = null;
        this.aprovadoPorId = null;
    }

    public void adicionarRedeSocial(RedeSocial redeSocial) {
        if (redeSocial == null) throw new IllegalArgumentException("Rede social não pode ser nula.");
        this.redesSociais.add(redeSocial);
    }

    public List<RedeSocial> getRedesSociais() {
        return Collections.unmodifiableList(redesSociais);
    }

    private void setNome(String nome) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do artista é obrigatório.");
        this.nome = nome;
    }

    private void setBio(String bio) {
        if (bio == null || bio.isBlank()) throw new IllegalArgumentException("Bio do artista é obrigatória.");
        this.bio = bio;
    }

    private void setEmail(String email) {
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("E-mail do artista é inválido.");
        this.email = email;
    }
}
