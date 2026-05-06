package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.time.LocalDate;
import recifecultural.dominio.espaco.espaco.EspacoId;

public class BloqueioAdministrativo {

    private BloqueioAdministrativoId id;
    private EspacoId espacoId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String justificativa;
    private boolean ativo;

    public BloqueioAdministrativo(EspacoId espacoId, LocalDate dataInicio, LocalDate dataFim, String justificativa) {
        this.id = BloqueioAdministrativoId.gerar();
        setEspacoId(espacoId);
        setPeriodo(dataInicio, dataFim);
        setJustificativa(justificativa);
        this.ativo = true;
    }

    public void atualizarInformacoes(String justificativa, LocalDate dataInicio, LocalDate dataFim) {
        setPeriodo(dataInicio, dataFim);
        setJustificativa(justificativa);
    }

    public void desativar() {
        this.ativo = false;
    }

    private void setEspacoId(EspacoId espacoId) {
        if (espacoId == null) {
            throw new IllegalArgumentException("O ID do espaço é obrigatório.");
        }
        this.espacoId = espacoId;
    }

    private void setPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio == null || dataFim == null) {
            throw new IllegalArgumentException("As datas de início e fim são obrigatórias.");
        }
        if (dataFim.isBefore(dataInicio)) {
            throw new IllegalArgumentException("A data final não pode ser anterior à data inicial.");
        }
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    private void setJustificativa(String justificativa) {
        if (justificativa == null || justificativa.trim().isEmpty()) {
            throw new IllegalArgumentException("Todo e qualquer bloqueio de um espaço tem de registrar um motivo (Justificativa obrigatória).");
        }

        if (justificativa.trim().length() < 10) {
            throw new IllegalArgumentException("A justificativa do bloqueio deve conter no mínimo 10 caracteres.");
        }
        this.justificativa = justificativa;
    }

    // Getters
    public BloqueioAdministrativoId getId() { return id; }
    public EspacoId getEspacoId() { return espacoId; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public String getJustificativa() { return justificativa; }
    public boolean isAtivo() { return ativo; }
}