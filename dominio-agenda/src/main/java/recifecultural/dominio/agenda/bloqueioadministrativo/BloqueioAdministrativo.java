package recifecultural.dominio.agenda.bloqueioadministrativo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import recifecultural.dominio.espaco.espaco.EspacoId;

public class BloqueioAdministrativo {

    private BloqueioAdministrativoId id;
    private EspacoId espacoId;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String justificativa;
    private boolean ativo;
    private List<UUID> eventosCancelados;

    public BloqueioAdministrativo(EspacoId espacoId, LocalDate dataInicio, LocalDate dataFim, String justificativa) {
        this.id = BloqueioAdministrativoId.gerar();
        setEspacoId(espacoId);
        setPeriodo(dataInicio, dataFim);
        setJustificativa(justificativa);
        this.ativo = true;
        this.eventosCancelados = new ArrayList<>();
    }

    /** Reconstruction constructor — preserva ID e flag ativo ao recarregar do banco. */
    public BloqueioAdministrativo(BloqueioAdministrativoId id, EspacoId espacoId,
                                   LocalDate dataInicio, LocalDate dataFim,
                                   String justificativa, boolean ativo,
                                   List<UUID> eventosCancelados) {
        if (id == null) throw new IllegalArgumentException("O ID do bloqueio é obrigatório.");
        this.id = id;
        setEspacoId(espacoId);
        setPeriodo(dataInicio, dataFim);
        setJustificativa(justificativa);
        this.ativo = ativo;
        this.eventosCancelados = eventosCancelados != null ? new ArrayList<>(eventosCancelados) : new ArrayList<>();
    }

    public CriadoEvento eventoCriacao() {
        return new CriadoEvento(this);
    }

    public void atualizarInformacoes(String justificativa, LocalDate dataInicio, LocalDate dataFim) {
        setPeriodo(dataInicio, dataFim);
        setJustificativa(justificativa);
    }

    public void desativar() {
        this.ativo = false;
    }

    public void registrarEventoCancelado(UUID eventoId) {
        if (eventoId != null) this.eventosCancelados.add(eventoId);
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

    public BloqueioAdministrativoId getId() { return id; }
    public EspacoId getEspacoId() { return espacoId; }
    public LocalDate getDataInicio() { return dataInicio; }
    public LocalDate getDataFim() { return dataFim; }
    public String getJustificativa() { return justificativa; }
    public boolean isAtivo() { return ativo; }
    public List<UUID> getEventosCancelados() { return Collections.unmodifiableList(eventosCancelados); }

    public static class BloqueioEvento {
        private final BloqueioAdministrativo bloqueio;

        private BloqueioEvento(BloqueioAdministrativo bloqueio) {
            this.bloqueio = bloqueio;
        }

        public BloqueioAdministrativo getBloqueio() {
            return bloqueio;
        }
    }

    public static class CriadoEvento extends BloqueioEvento {
        private CriadoEvento(BloqueioAdministrativo bloqueio) {
            super(bloqueio);
        }
    }
}
