package recifecultural.dominio.agenda.espectador;

public class Espectador {

    private final EspectadorId id;
    private String nome;
    private String email;

    public Espectador(EspectadorId id, String nome, String email) {
        this.id = id;
        if (nome == null || nome.isBlank())
            throw new IllegalArgumentException("Nome do espectador é obrigatório.");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("E-mail do espectador é obrigatório.");
        this.nome = nome;
        this.email = email;
    }

    public EspectadorId getId() { return id; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
}
