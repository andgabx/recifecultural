package recifecultural.dominio.agenda.evento;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Evento {
    private final UUID id;
    private final UUID promotorId;
    private final UUID localId;

    private String titulo;
    private String descricaoCurta;
    private String descricaoLonga;

    private Periodo periodo;
    private final List<LocalDateTime> datasApresentacao;
    private URI enderecoIngresso;

    private Preco preco;

    private StatusEvento status;
    private FeedbackReprovacao feedbackReprovacao;
    private String motivoCancelamento;

    public UUID getId() { return id; }

    public UUID getPromotorId() { return promotorId; }

    public UUID getLocalId() {
        return localId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public String getDescricaoLonga() {
        return descricaoLonga;
    }

    public Periodo getPeriodo() {
        return periodo;
    }

    public URI getEnderecoIngresso() {
        return enderecoIngresso;
    }

    public Preco getPreco() {
        return preco;
    }

    public StatusEvento getStatus() {
        return status;
    }

    public FeedbackReprovacao getFeedbackReprovacao() {
        return feedbackReprovacao;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public List<LocalDateTime> getDatasApresentacao() {
        return Collections.unmodifiableList(datasApresentacao);
    }

    private void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        this.titulo = titulo;
    }

    public Evento(
            UUID promotorId,
            UUID localId,
            String titulo,
            String descricaoCurta,
            String descricaoLonga,
            Periodo periodo,
            URI enderecoIngresso,
            Preco preco
    ) {
        this.id = UUID.randomUUID();
        this.promotorId = promotorId;
        this.localId = localId;

        setTitulo(titulo);
        this.descricaoCurta = descricaoCurta;
        this.descricaoLonga = descricaoLonga;

        this.periodo = periodo;
        this.datasApresentacao = new ArrayList<>();
        this.enderecoIngresso = enderecoIngresso;
        this.preco = preco;

        this.status = StatusEvento.RASCUNHO;
    }

    public void submeterParaAnalise() {
        if (datasApresentacao.isEmpty())
            throw new IllegalStateException("O evento deve ter pelo menos uma data de apresentação para ser submetido à análise.");
        this.status = StatusEvento.EM_ANALISE;
    }

    public void aprovar() {
        exigirStatusEmAnalise("aprovar");
        this.status = StatusEvento.APROVADO;
    }

    public void reprovar(FeedbackReprovacao feedback) {
        exigirStatusEmAnalise("reprovar");
        this.feedbackReprovacao = feedback;
        this.status = StatusEvento.REPROVADO;
    }

    public void cancelar(String motivo) {
        if (this.status == StatusEvento.REALIZADO) {
            throw new IllegalStateException("Não é possível cancelar um evento que já foi realizado.");
        }
        if (motivo == null || motivo.isBlank()) {
            throw new IllegalArgumentException("Motivo do cancelamento é obrigatório.");
        }
        this.status = StatusEvento.CANCELADO;
        this.motivoCancelamento = motivo;
    }

    private void exigirStatusEmAnalise(String acao) {
        if (this.status != StatusEvento.EM_ANALISE)
            throw new IllegalStateException("Não é possível " + acao + " um evento com status " + this.status + ".");
    }

    public void programarApresentacao(LocalDateTime dataHora) {
        if (dataHora == null) throw new IllegalArgumentException("Data nula.");
        if (!periodo.contemData(dataHora))
            throw new IllegalArgumentException("Apresentação fora do período do evento.");
        this.datasApresentacao.add(dataHora);
    }
}