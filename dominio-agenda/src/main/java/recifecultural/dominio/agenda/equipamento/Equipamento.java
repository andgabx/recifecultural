package recifecultural.dominio.agenda.equipamento;

import recifecultural.dominio.agenda.espaco.EspacoId;
import java.util.UUID;

public class Equipamento {
    private final EquipamentoId id;
    private final EspacoId espacoId;
    private String nome;
    private StatusEquipamento status;
    private UUID eventoAlocadoId; // ID do evento que está usando a peça

    public Equipamento(EspacoId espacoId, String nome) {
        if (espacoId == null) throw new IllegalArgumentException("Espaço é obrigatório.");
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome do equipamento é obrigatório.");

        this.id = EquipamentoId.novo();
        this.espacoId = espacoId;
        this.nome = nome;
        this.status = StatusEquipamento.DISPONIVEL;
        this.eventoAlocadoId = null;
    }

    public void alocarParaEvento(UUID eventoId) {
        if (this.status != StatusEquipamento.DISPONIVEL) {
            throw new IllegalStateException("O equipamento '" + this.nome + "' não está disponível para alocação.");
        }
        this.status = StatusEquipamento.ALOCADO;
        this.eventoAlocadoId = eventoId;
    }

    public void enviarParaManutencao() {
        this.status = StatusEquipamento.EM_MANUTENCAO;
        // Não limpamos o eventoAlocadoId aqui de propósito!
        // O serviço usará essa informação para alertar o evento antes de desvincular.
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
}