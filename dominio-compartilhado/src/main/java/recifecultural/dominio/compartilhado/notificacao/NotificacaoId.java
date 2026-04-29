package recifecultural.dominio.compartilhado.notificacao;

import java.util.UUID;

public record NotificacaoId(UUID valor) {
    public NotificacaoId {
        if (valor == null) {
            throw new IllegalArgumentException("O valor do ID da notificação não pode ser nulo.");
        }
    }

    public static NotificacaoId gerar() {
        return new NotificacaoId(UUID.randomUUID());
    }

    public static NotificacaoId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O valor em texto do ID não pode estar vazio.");
        }
        return new NotificacaoId(UUID.fromString(valor));
    }
}