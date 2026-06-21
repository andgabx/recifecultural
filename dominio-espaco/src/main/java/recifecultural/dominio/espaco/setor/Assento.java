package recifecultural.dominio.espaco.setor;

import java.util.UUID;

public class Assento {

    private final UUID id;
    private final String codigo;
    private final String fileira;
    private final int numero;
    private StatusAssento status;
    private MotivoIndisponibilidade motivoIndisponibilidade;
    private String descricao;
    private int versao;

    public Assento(String fileira, int numero) {
        if (fileira == null || fileira.isBlank()) throw new IllegalArgumentException("Fileira é obrigatória.");
        if (numero <= 0) throw new IllegalArgumentException("Número deve ser positivo.");
        this.id = UUID.randomUUID();
        this.fileira = fileira;
        this.numero = numero;
        this.codigo = fileira.toUpperCase() + numero;
        this.status = StatusAssento.LIVRE;
        this.motivoIndisponibilidade = null;
        this.versao = 0;
    }

    public Assento(UUID id, String codigo, String fileira, int numero, StatusAssento status, MotivoIndisponibilidade motivoIndisponibilidade, int versao) {
        this.id = id; this.codigo = codigo; this.fileira = fileira;
        this.numero = numero; this.status = status; 
        this.motivoIndisponibilidade = motivoIndisponibilidade;
        this.versao = versao;
    }

    void marcarPreReservado() {
        if (this.status != StatusAssento.LIVRE)
            throw new IllegalStateException("Assento " + codigo + " não está disponível. Status atual: " + status);
        this.status = StatusAssento.PRE_RESERVADO;
    }

    void liberar() {
        if (this.status == StatusAssento.INDISPONIVEL)
            throw new IllegalStateException("Assento INDISPONIVEL não pode ser liberado diretamente.");
        this.status = StatusAssento.LIVRE;
        this.motivoIndisponibilidade = null;
    }

    void ocupar() {
        if (this.status != StatusAssento.PRE_RESERVADO)
            throw new IllegalStateException("Assento deve estar pré-reservado para ser INDISPONIVEL.");
        this.status = StatusAssento.INDISPONIVEL;
    }

    public void abrirChamado(MotivoIndisponibilidade motivo, String descricao) {
        if (this.status != StatusAssento.LIVRE) {
            throw new IllegalStateException("Apenas cadeiras DISPONIVEIS podem abrir chamado. Status atual: " + this.status);
        }
        if (motivo == null) {
            throw new IllegalArgumentException("Motivo de indisponibilidade é obrigatório (RN 4).");
        }
        this.status = StatusAssento.INDISPONIVEL;
        this.motivoIndisponibilidade = motivo;
        this.descricao = descricao;
    }

    public void iniciarManutencao() {
        if (this.status != StatusAssento.INDISPONIVEL) {
            throw new IllegalStateException("Transição inválida: A cadeira deve estar INDISPONIVEL para entrar em manutenção.");
        }
        this.status = StatusAssento.EM_MANUTENCAO;
    }

    public void resolverManutencao() {
        if (this.status != StatusAssento.EM_MANUTENCAO) {
            throw new IllegalStateException("Transição inválida: A cadeira deve estar EM_MANUTENCAO para ser resolvida.");
        }
        this.status = StatusAssento.LIVRE;
        this.motivoIndisponibilidade = null;
        this.descricao = null;
    }


    void bloquear(MotivoIndisponibilidade motivo) {
        if (this.status == StatusAssento.INDISPONIVEL)
            throw new IllegalStateException("Não é possível bloquear assento INDISPONIVEL.");
        if (motivo == null)
            throw new IllegalArgumentException("Motivo de indisponibilidade é obrigatório para bloquear.");
        this.status = StatusAssento.OCUPADO;
        this.motivoIndisponibilidade = motivo;
    }

        public void bloquearAdministrativamente(MotivoIndisponibilidade motivo, String descricao) {
        if (this.status != StatusAssento.LIVRE) {
            throw new IllegalStateException("Apenas cadeiras DISPONIVEIS podem ser bloqueadas diretamente.");
        }
        if (motivo == null) {
            throw new IllegalArgumentException("Motivo de indisponibilidade é obrigatório (RN 4).");
        }
        this.status = StatusAssento.OCUPADO;
        this.motivoIndisponibilidade = motivo;
        this.descricao = descricao;
    }

    public void desbloquearAdministrativamente() {
        if (this.status != StatusAssento.OCUPADO) {
            throw new IllegalStateException("Apenas cadeiras BLOQUEADAS podem ser desbloqueadas administrativamente.");
        }
        this.status = StatusAssento.LIVRE;
        this.motivoIndisponibilidade = null;
        this.descricao = null;
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getFileira() { return fileira; }
    public int getNumero() { return numero; }
    public StatusAssento getStatus() { return status; }
    public MotivoIndisponibilidade getMotivoIndisponibilidade() { return motivoIndisponibilidade; }
    public int getVersao() { return versao; }
}
