package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Patrocinio {

    private static final BigDecimal PISO_PRECO_SOCIAL = new BigDecimal("1.00");
    private static final BigDecimal PERCENTUAL_MULTA = new BigDecimal("0.20");
    private static final BigDecimal PERCENTUAL_REEMBOLSO_PARCIAL = new BigDecimal("0.50");
    private static final BigDecimal PERCENTUAL_REEMBOLSO_PATROCINADOR_MULTA = new BigDecimal("0.80");

    private final PatrocinioId id;
    private final EventoId eventoId;
    private final String patrocinadorNome;
    private final String categoriaPatrocinio;
    private final TipoPatrocinio tipo;
    private final ModalidadeContribuicao modalidade;
    private final BigDecimal valorContribuicao;
    private final LocalDateTime dataEvento;
    private StatusPatrocinio status;
    private BigDecimal valorReembolsado;
    private BigDecimal multaAplicada;

    public Patrocinio(PatrocinioId id,
                      EventoId eventoId,
                      String patrocinadorNome,
                      String categoriaPatrocinio,
                      TipoPatrocinio tipo,
                      ModalidadeContribuicao modalidade,
                      BigDecimal valorContribuicao,
                      LocalDateTime dataEvento) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        notNull(eventoId, "O id do evento não pode ser nulo.");
        notBlank(patrocinadorNome, "O nome do patrocinador não pode ser vazio.");
        notBlank(categoriaPatrocinio, "A categoria do patrocínio não pode ser vazia.");
        notNull(tipo, "O tipo do patrocínio não pode ser nulo.");
        notNull(modalidade, "A modalidade de contribuição não pode ser nula.");
        notNull(valorContribuicao, "O valor da contribuição não pode ser nulo.");
        isTrue(valorContribuicao.compareTo(BigDecimal.ZERO) > 0, "O valor da contribuição deve ser maior que zero.");
        notNull(dataEvento, "A data do evento não pode ser nula.");

        this.id = id;
        this.eventoId = eventoId;
        this.patrocinadorNome = patrocinadorNome;
        this.categoriaPatrocinio = categoriaPatrocinio;
        this.tipo = tipo;
        this.modalidade = modalidade;
        this.valorContribuicao = valorContribuicao;
        this.dataEvento = dataEvento;
        this.status = StatusPatrocinio.PROPOSTA;
    }

    public void ativar() {
        isTrue(status == StatusPatrocinio.PROPOSTA, "Apenas patrocínios com status PROPOSTA podem ser ativados.");
        this.status = StatusPatrocinio.ATIVO;
    }

    public void encerrar() {
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem ser encerrados.");
        this.status = StatusPatrocinio.ENCERRADO;
    }

    public ResultadoCancelamento cancelarPorEvento(LocalDateTime agora) {
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem ser cancelados por evento.");
        notNull(agora, "A data/hora atual não pode ser nula.");

        long diasRestantes = ChronoUnit.DAYS.between(agora.toLocalDate(), dataEvento.toLocalDate());

        BigDecimal reembolso;
        String motivo;

        if (diasRestantes > 7) {
            reembolso = valorContribuicao;
            motivo = "Cancelamento pelo evento com mais de 7 dias de antecedência. Reembolso integral.";
        } else if (diasRestantes >= 2) {
            reembolso = valorContribuicao.multiply(PERCENTUAL_REEMBOLSO_PARCIAL);
            motivo = "Cancelamento pelo evento entre 2 e 7 dias de antecedência. Reembolso de 50%.";
        } else {
            reembolso = BigDecimal.ZERO;
            motivo = "Cancelamento pelo evento com menos de 2 dias de antecedência. Sem reembolso.";
        }

        this.valorReembolsado = reembolso;
        this.multaAplicada = BigDecimal.ZERO;
        this.status = StatusPatrocinio.CANCELADO_EVENTO;

        return new ResultadoCancelamento(reembolso, BigDecimal.ZERO, motivo);
    }

    public ResultadoCancelamento cancelarPorPatrocinador(LocalDateTime agora) {
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem ser cancelados pelo patrocinador.");
        notNull(agora, "A data/hora atual não pode ser nula.");

        long diasRestantes = ChronoUnit.DAYS.between(agora.toLocalDate(), dataEvento.toLocalDate());

        BigDecimal reembolso;
        BigDecimal multa;
        String motivo;

        if (diasRestantes > 15) {
            reembolso = valorContribuicao;
            multa = BigDecimal.ZERO;
            motivo = "Cancelamento pelo patrocinador com mais de 15 dias de antecedência. Sem penalidade.";
        } else {
            multa = valorContribuicao.multiply(PERCENTUAL_MULTA);
            reembolso = valorContribuicao.multiply(PERCENTUAL_REEMBOLSO_PATROCINADOR_MULTA);
            motivo = "Cancelamento pelo patrocinador com até 15 dias de antecedência. Multa de 20% aplicada.";
        }

        this.valorReembolsado = reembolso;
        this.multaAplicada = multa;
        this.status = StatusPatrocinio.CANCELADO_PATROCINADOR;

        return new ResultadoCancelamento(reembolso, multa, motivo);
    }

    public ResultadoSubsidio calcularSubsidio(BigDecimal precoSocialAtual) {
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem calcular subsídio.");
        isTrue(modalidade == ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL,
                "Apenas patrocínios com modalidade SUBSIDIO_INGRESSO_SOCIAL podem calcular subsídio.");
        notNull(precoSocialAtual, "O preço social atual não pode ser nulo.");
        isTrue(precoSocialAtual.compareTo(BigDecimal.ZERO) > 0, "O preço social atual deve ser maior que zero.");

        BigDecimal novoPreco = precoSocialAtual.subtract(valorContribuicao);
        boolean pisoAplicado = novoPreco.compareTo(PISO_PRECO_SOCIAL) < 0;

        if (pisoAplicado) {
            novoPreco = PISO_PRECO_SOCIAL;
        }

        return new ResultadoSubsidio(novoPreco, pisoAplicado);
    }

    public PatrocinioId getId() { return id; }
    public EventoId getEventoId() { return eventoId; }
    public String getPatrocinadorNome() { return patrocinadorNome; }
    public String getCategoriaPatrocinio() { return categoriaPatrocinio; }
    public TipoPatrocinio getTipo() { return tipo; }
    public ModalidadeContribuicao getModalidade() { return modalidade; }
    public BigDecimal getValorContribuicao() { return valorContribuicao; }
    public LocalDateTime getDataEvento() { return dataEvento; }
    public StatusPatrocinio getStatus() { return status; }
    public BigDecimal getValorReembolsado() { return valorReembolsado; }
    public BigDecimal getMultaAplicada() { return multaAplicada; }
}
