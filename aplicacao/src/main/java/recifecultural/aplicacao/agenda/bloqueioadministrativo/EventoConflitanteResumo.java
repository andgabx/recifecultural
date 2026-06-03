package recifecultural.aplicacao.agenda.bloqueioadministrativo;

public record EventoConflitanteResumo(
        String id,
        String titulo,
        String periodoInicio,
        String periodoFim,
        int totalEspectadores,
        java.math.BigDecimal totalReembolso) {}
