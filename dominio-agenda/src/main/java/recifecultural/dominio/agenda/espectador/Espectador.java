package recifecultural.dominio.agenda.espectador;

import java.util.UUID;

public class Espectador {

    private final UUID id;
    private String nome;
    private String email;

    public Espectador(UUID id, String nome, String email) {
        this.id = id;
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do espectador é obrigatório.");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("E-mail do espectador é obrigatório.");
        this.nome = nome;
        this.email = email;
    }

    public UUID getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
