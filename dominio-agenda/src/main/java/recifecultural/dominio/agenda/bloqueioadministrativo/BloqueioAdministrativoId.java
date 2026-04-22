package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.util.UUID;

public record BloqueioAdministrativoId(UUID valor) {

    public BloqueioAdministrativoId {
        if (valor == null) {
            throw new IllegalArgumentException("O valor do ID do bloqueio não pode ser nulo.");
        }
    }

    public static BloqueioAdministrativoId gerar() {
        return new BloqueioAdministrativoId(UUID.randomUUID());
    }

    public static BloqueioAdministrativoId de(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("O valor em texto do ID não pode estar vazio.");
        }
        return new BloqueioAdministrativoId(UUID.fromString(valor));
    }
}