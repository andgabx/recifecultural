package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class Patrocinio {

    private static final BigDecimal PISO_PRECO_SOCIAL = new BigDecimal("1.00");

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

    // Reconstruction constructor — JPA round-trip, preserves persisted state without side effects
    public Patrocinio(PatrocinioId id,
                      EventoId eventoId,
                      String patrocinadorNome,
                      String categoriaPatrocinio,
                      TipoPatrocinio tipo,
                      ModalidadeContribuicao modalidade,
                      BigDecimal valorContribuicao,
                      LocalDateTime dataEvento,
                      StatusPatrocinio status,
                      BigDecimal valorReembolsado,
                      BigDecimal multaAplicada) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        notNull(eventoId, "O id do evento não pode ser nulo.");
        notBlank(patrocinadorNome, "O nome do patrocinador não pode ser vazio.");
        notBlank(categoriaPatrocinio, "A categoria do patrocínio não pode ser vazia.");
        notNull(tipo, "O tipo do patrocínio não pode ser nulo.");
        notNull(modalidade, "A modalidade de contribuição não pode ser nula.");
        notNull(valorContribuicao, "O valor da contribuição não pode ser nulo.");
        notNull(dataEvento, "A data do evento não pode ser nula.");
        notNull(status, "O status do patrocínio não pode ser nulo.");

        this.id = id;
        this.eventoId = eventoId;
        this.patrocinadorNome = patrocinadorNome;
        this.categoriaPatrocinio = categoriaPatrocinio;
        this.tipo = tipo;
        this.modalidade = modalidade;
        this.valorContribuicao = valorContribuicao;
        this.dataEvento = dataEvento;
        this.status = status;
        this.valorReembolsado = valorReembolsado;
        this.multaAplicada = multaAplicada;
    }

    public void ativar() {
        isTrue(status == StatusPatrocinio.PROPOSTA, "Apenas patrocínios com status PROPOSTA podem ser ativados.");
        this.status = StatusPatrocinio.ATIVO;
    }

    public void encerrar() {
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem ser encerrados.");
        this.status = StatusPatrocinio.ENCERRADO;
    }

    // Strategy: delega o cálculo a uma EstrategiaCancelamentoPatrocinio injetada.
    // O agregado mantém invariantes (status ATIVO, valor reembolsado, status final)
    // e a estratégia encapsula a política financeira específica.
    public CanceladoEvento cancelar(EstrategiaCancelamentoPatrocinio estrategia, LocalDateTime agora) {
        notNull(estrategia, "A estratégia de cancelamento não pode ser nula.");
        notNull(agora, "A data/hora atual não pode ser nula.");
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem ser cancelados.");

        ResultadoCalculoCancelamento resultado = estrategia.calcular(valorContribuicao, dataEvento, agora);

        this.valorReembolsado = resultado.reembolso();
        this.multaAplicada = resultado.multa();
        this.status = estrategia.statusFinal();

        return new CanceladoEvento(this, resultado.reembolso(), resultado.multa(), resultado.motivo());
    }

    public CanceladoEvento cancelarPorEvento(LocalDateTime agora) {
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem ser cancelados por evento.");
        return cancelar(new EstrategiaCancelamentoPorEvento(), agora);
    }

    public CanceladoEvento cancelarPorPatrocinador(LocalDateTime agora) {
        isTrue(status == StatusPatrocinio.ATIVO, "Apenas patrocínios com status ATIVO podem ser cancelados pelo patrocinador.");
        return cancelar(new EstrategiaCancelamentoPorPatrocinador(), agora);
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

    public static class PatrocinioEvento {
        private final Patrocinio patrocinio;

        private PatrocinioEvento(Patrocinio patrocinio) {
            this.patrocinio = patrocinio;
        }

        public Patrocinio getPatrocinio() {
            return patrocinio;
        }
    }

    public static class CanceladoEvento extends PatrocinioEvento {
        private final BigDecimal reembolso;
        private final BigDecimal multa;
        private final String motivo;

        private CanceladoEvento(Patrocinio patrocinio, BigDecimal reembolso, BigDecimal multa, String motivo) {
            super(patrocinio);
            this.reembolso = reembolso;
            this.multa = multa;
            this.motivo = motivo;
        }

        public BigDecimal getReembolso() {
            return reembolso;
        }

        public BigDecimal getMulta() {
            return multa;
        }

        public String getMotivo() {
            return motivo;
        }
    }
}
