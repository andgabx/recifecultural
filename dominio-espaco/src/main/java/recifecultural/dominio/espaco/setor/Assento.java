package recifecultural.dominio.espaco.setor;

import java.util.UUID;

public class Assento {

    private final UUID id;
    private final String codigo;
    private final String fileira;
    private final int numero;
    private StatusAssento status;
    private MotivoIndisponibilidadeAssento motivoIndisponibilidade;
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

    public Assento(UUID id, String codigo, String fileira, int numero, StatusAssento status, MotivoIndisponibilidadeAssento motivoIndisponibilidade, int versao) {
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
        if (this.status == StatusAssento.OCUPADO)
            throw new IllegalStateException("Assento ocupado não pode ser liberado diretamente.");
        this.status = StatusAssento.LIVRE;
        this.motivoIndisponibilidade = null;
    }

    void ocupar() {
        if (this.status != StatusAssento.PRE_RESERVADO)
            throw new IllegalStateException("Assento deve estar pré-reservado para ser ocupado.");
        this.status = StatusAssento.OCUPADO;
    }

    void bloquear(MotivoIndisponibilidadeAssento motivo) {
        if (this.status == StatusAssento.OCUPADO)
            throw new IllegalStateException("Não é possível bloquear assento ocupado.");
        if (motivo == null)
            throw new IllegalArgumentException("Motivo de indisponibilidade é obrigatório para bloquear.");
        this.status = StatusAssento.BLOQUEADO;
        this.motivoIndisponibilidade = motivo;
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getFileira() { return fileira; }
    public int getNumero() { return numero; }
    public StatusAssento getStatus() { return status; }
    public MotivoIndisponibilidadeAssento getMotivoIndisponibilidade() { return motivoIndisponibilidade; }
    public int getVersao() { return versao; }
}
