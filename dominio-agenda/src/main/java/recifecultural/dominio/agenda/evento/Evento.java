package recifecultural.dominio.agenda.evento;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Evento {
    private static final int LIMITE_DIAS_REPROVACAO = 30;

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
    private LocalDateTime dataSubmissao;

    public UUID getId() { return id; }
    public UUID getPromotorId() { return promotorId; }
    public UUID getLocalId() { return localId; }
    public String getTitulo() { return titulo; }
    public String getDescricaoCurta() { return descricaoCurta; }
    public String getDescricaoLonga() { return descricaoLonga; }
    public Periodo getPeriodo() { return periodo; }
    public URI getEnderecoIngresso() { return enderecoIngresso; }
    public Preco getPreco() { return preco; }
    public StatusEvento getStatus() { return status; }
    public FeedbackReprovacao getFeedbackReprovacao() { return feedbackReprovacao; }
    public LocalDateTime getDataSubmissao() { return dataSubmissao; }

    public List<LocalDateTime> getDatasApresentacao() {
        return Collections.unmodifiableList(datasApresentacao);
    }

    private void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank())
            throw new IllegalArgumentException("Título é obrigatório.");
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

    public void submeterParaAnalise(LocalDateTime agora) {
        if (datasApresentacao.isEmpty())
            throw new IllegalStateException("O evento deve ter pelo menos uma data de apresentação para ser submetido à análise.");
        this.dataSubmissao = agora;
        this.status = StatusEvento.EM_ANALISE;
    }

    public void aprovar() {
        exigirStatusEmAnalise("aprovar");
        this.status = StatusEvento.APROVADO;
    }

    public void reprovar(FeedbackReprovacao feedback, LocalDateTime agora) {
        exigirStatusEmAnalise("reprovar");
        if (ChronoUnit.DAYS.between(dataSubmissao, agora) > LIMITE_DIAS_REPROVACAO)
            throw new IllegalStateException(
                "Prazo de reprovação de " + LIMITE_DIAS_REPROVACAO + " dias expirou.");
        this.feedbackReprovacao = feedback;
        this.status = StatusEvento.REPROVADO;
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
