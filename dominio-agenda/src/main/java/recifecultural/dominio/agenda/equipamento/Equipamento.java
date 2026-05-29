package recifecultural.dominio.agenda.equipamento;

import java.util.Optional;
import java.util.UUID;

import recifecultural.dominio.espaco.espaco.EspacoId;

public class Equipamento {
    private final EquipamentoId id;
    private final EspacoId espacoId;
    private String nome;
    private StatusEquipamento status;
    private UUID eventoAlocadoId;

    public Equipamento(EspacoId espacoId, String nome) {
        if (espacoId == null) throw new IllegalArgumentException("Espaço é obrigatório.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do equipamento é obrigatório.");

        this.id = EquipamentoId.novo();
        this.espacoId = espacoId;
        this.nome = nome;
        this.status = StatusEquipamento.DISPONIVEL;
        this.eventoAlocadoId = null;
    }

    public Equipamento(EquipamentoId id, EspacoId espacoId, String nome,
                       StatusEquipamento status, UUID eventoAlocadoId) {
        this.id = id;
        this.espacoId = espacoId;
        this.nome = nome;
        this.status = status;
        this.eventoAlocadoId = eventoAlocadoId;
    }

    public void alocarParaEvento(UUID eventoId) {
        if (this.status != StatusEquipamento.DISPONIVEL) {
            throw new IllegalStateException("O equipamento '" + this.nome + "' não está disponível para alocação.");
        }
        this.status = StatusEquipamento.ALOCADO;
        this.eventoAlocadoId = eventoId;
    }

    public Optional<EmManutencaoEvento> enviarParaManutencao() {
        boolean estavaAlocado = this.status == StatusEquipamento.ALOCADO;
        UUID eventoOrigem = this.eventoAlocadoId;
        this.status = StatusEquipamento.EM_MANUTENCAO;
        if (estavaAlocado) {
            return Optional.of(new EmManutencaoEvento(this, eventoOrigem));
        }
        return Optional.empty();
    }

    public void liberar() {
        this.status = StatusEquipamento.DISPONIVEL;
        this.eventoAlocadoId = null;
    }

    public EquipamentoId getId() { return id; }
    public EspacoId getEspacoId() { return espacoId; }
    public String getNome() { return nome; }
    public StatusEquipamento getStatus() { return status; }
    public UUID getEventoAlocadoId() { return eventoAlocadoId; }

    public static class EquipamentoEvento {
        private final Equipamento equipamento;

        private EquipamentoEvento(Equipamento equipamento) {
            this.equipamento = equipamento;
        }

        public Equipamento getEquipamento() {
            return equipamento;
        }
    }

    public static class EmManutencaoEvento extends EquipamentoEvento {
        private final UUID eventoAlocadoId;

        private EmManutencaoEvento(Equipamento equipamento, UUID eventoAlocadoId) {
            super(equipamento);
            this.eventoAlocadoId = eventoAlocadoId;
        }

        public UUID getEventoAlocadoId() {
            return eventoAlocadoId;
        }
    }
}
