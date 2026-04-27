package recifecultural.dominio.agenda.setor;

import java.util.UUID;

public class Assento {

    private final UUID id;
    private final String codigo;
    private final String fileira;
    private final int numero;
    private StatusAssento status;
    private int versao;

    public Assento(String fileira, int numero) {
        if (fileira == null || fileira.isBlank()) throw new IllegalArgumentException("Fileira é obrigatória.");
        if (numero <= 0) throw new IllegalArgumentException("Número deve ser positivo.");
        this.id = UUID.randomUUID();
        this.fileira = fileira;
        this.numero = numero;
        this.codigo = fileira.toUpperCase() + numero;
        this.status = StatusAssento.LIVRE;
        this.versao = 0;
    }

    public Assento(UUID id, String codigo, String fileira, int numero,
                   StatusAssento status, int versao) {
        this.id = id; this.codigo = codigo; this.fileira = fileira;
        this.numero = numero; this.status = status; this.versao = versao;
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
    }

    void ocupar() {
        if (this.status != StatusAssento.PRE_RESERVADO)
            throw new IllegalStateException("Assento deve estar pré-reservado para ser ocupado.");
        this.status = StatusAssento.OCUPADO;
    }

    void bloquear() {
        if (this.status == StatusAssento.OCUPADO)
            throw new IllegalStateException("Não é possível bloquear assento ocupado.");
        this.status = StatusAssento.BLOQUEADO;
    }

    public UUID getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getFileira() { return fileira; }
    public int getNumero() { return numero; }
    public StatusAssento getStatus() { return status; }
    public int getVersao() { return versao; }
}