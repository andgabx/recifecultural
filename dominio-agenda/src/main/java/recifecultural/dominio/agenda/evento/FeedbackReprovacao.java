package recifecultural.dominio.agenda.evento;

public record FeedbackReprovacao(String texto) {

    private static final int TAMANHO_MINIMO = 20;

    public FeedbackReprovacao {
        if (texto == null || texto.isBlank())
            throw new IllegalArgumentException("Feedback de reprovação é obrigatório.");
        if (texto.trim().length() < TAMANHO_MINIMO)
            throw new IllegalArgumentException("Feedback deve ter no mínimo " + TAMANHO_MINIMO + " caracteres.");
    }
}
