package recifecultural.dominio.agenda.notificacao;

import java.util.UUID;

public record NotificacaoId(UUID valor) {
    public NotificacaoId {
        if (valor == null) {
            throw new IllegalArgumentException("O valor do ID da notificação não pode ser nulo.");
        }
    }

    public static recifecultural.dominio.agenda.notificacao.NotificacaoId gerar() {
        return new recifecultural.dominio.agenda.notificacao.NotificacaoId(UUID.randomUUID());
    }

    public static recifecultural.dominio.agenda.notificacao.NotificacaoId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O valor em texto do ID não pode estar vazio.");
        }
        return new recifecultural.dominio.agenda.notificacao.NotificacaoId(UUID.fromString(valor));
    }
}