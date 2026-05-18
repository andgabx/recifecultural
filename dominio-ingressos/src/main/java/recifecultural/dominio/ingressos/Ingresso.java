package recifecultural.dominio.ingressos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class Ingresso {

    private final IngressoId id;
    private final UUID eventoId;
    private final UUID assentoId;
    private final LocalDateTime dataHoraApresentacao;
    private final TipoIngresso tipo;
    private StatusIngresso status;
    private final BigDecimal valorPago;
    private final String codigoQr;
    private final String codigoTransacao;
    private final MetodoPagamento metodoPagamento;
    private final LocalDateTime dataCompra;
    private BigDecimal valorReembolsado;

    public Ingresso(IngressoId id,
                    UUID eventoId,
                    LocalDateTime dataHoraApresentacao,
                    TipoIngresso tipo,
                    BigDecimal valorPago,
                    String codigoQr,
                    String codigoTransacao,
                    MetodoPagamento metodoPagamento) {
        this(id, eventoId, null, dataHoraApresentacao, tipo, valorPago, codigoQr, codigoTransacao, metodoPagamento);
    }

    public Ingresso(IngressoId id,
                    UUID eventoId,
                    UUID assentoId,
                    LocalDateTime dataHoraApresentacao,
                    TipoIngresso tipo,
                    BigDecimal valorPago,
                    String codigoQr,
                    String codigoTransacao,
                    MetodoPagamento metodoPagamento) {
        notNull(id, "O id do ingresso não pode ser nulo.");
        notNull(eventoId, "O id do evento não pode ser nulo.");
        notNull(dataHoraApresentacao, "A data e hora da apresentação não podem ser nulas.");
        notNull(tipo, "O tipo do ingresso não pode ser nulo.");
        notNull(valorPago, "O valor pago não pode ser nulo.");
        isTrue(valorPago.compareTo(BigDecimal.ZERO) > 0, "O valor pago deve ser maior que zero.");
        notNull(codigoQr, "O código QR não pode ser nulo.");
        notNull(codigoTransacao, "O código de transação não pode ser nulo.");
        notNull(metodoPagamento, "O método de pagamento não pode ser nulo.");

        this.id = id;
        this.eventoId = eventoId;
        this.assentoId = assentoId;
        this.dataHoraApresentacao = dataHoraApresentacao;
        this.tipo = tipo;
        this.valorPago = valorPago;
        this.codigoQr = codigoQr;
        this.codigoTransacao = codigoTransacao;
        this.metodoPagamento = metodoPagamento;
        this.dataCompra = LocalDateTime.now();
        this.status = StatusIngresso.ATIVO;
    }

    // Reconstruction constructor — JPA round-trip, preserves persisted state without side effects
    public Ingresso(IngressoId id,
                    UUID eventoId,
                    UUID assentoId,
                    LocalDateTime dataHoraApresentacao,
                    TipoIngresso tipo,
                    BigDecimal valorPago,
                    String codigoQr,
                    String codigoTransacao,
                    MetodoPagamento metodoPagamento,
                    LocalDateTime dataCompra,
                    StatusIngresso status,
                    BigDecimal valorReembolsado) {
        notNull(id, "O id do ingresso não pode ser nulo.");
        notNull(eventoId, "O id do evento não pode ser nulo.");
        notNull(dataHoraApresentacao, "A data e hora da apresentação não podem ser nulas.");
        notNull(tipo, "O tipo do ingresso não pode ser nulo.");
        notNull(valorPago, "O valor pago não pode ser nulo.");
        notNull(codigoQr, "O código QR não pode ser nulo.");
        notNull(codigoTransacao, "O código de transação não pode ser nulo.");
        notNull(metodoPagamento, "O método de pagamento não pode ser nulo.");
        notNull(dataCompra, "A data de compra não pode ser nula.");
        notNull(status, "O status do ingresso não pode ser nulo.");

        this.id = id;
        this.eventoId = eventoId;
        this.assentoId = assentoId;
        this.dataHoraApresentacao = dataHoraApresentacao;
        this.tipo = tipo;
        this.valorPago = valorPago;
        this.codigoQr = codigoQr;
        this.codigoTransacao = codigoTransacao;
        this.metodoPagamento = metodoPagamento;
        this.dataCompra = dataCompra;
        this.status = status;
        this.valorReembolsado = valorReembolsado;
    }

    public CompradoEvento eventoCompra() {
        return new CompradoEvento(this);
    }

    public BigDecimal calcularReembolso(LocalDateTime agora) {
        long diasRestantes = ChronoUnit.DAYS.between(agora.toLocalDate(), dataHoraApresentacao.toLocalDate());

        if (diasRestantes > 7) {
            return valorPago;
        } else if (diasRestantes >= 2) {
            return valorPago.multiply(new BigDecimal("0.5"));
        } else {
            return BigDecimal.ZERO;
        }
    }

    public ReembolsadoEvento reembolsar(BigDecimal valor) {
        isTrue(status == StatusIngresso.ATIVO, "Apenas ingressos com status ATIVO podem ser reembolsados.");
        isTrue(valor.compareTo(BigDecimal.ZERO) > 0, "O valor de reembolso deve ser maior que zero.");
        this.status = StatusIngresso.REEMBOLSADO;
        this.valorReembolsado = valor;
        return new ReembolsadoEvento(this);
    }

    public void marcarComoUtilizado() {
        isTrue(status == StatusIngresso.ATIVO, "Apenas ingressos com status ATIVO podem ser marcados como utilizados.");
        this.status = StatusIngresso.UTILIZADO;
    }

    public IngressoId getId() { return id; }
    public UUID getEventoId() { return eventoId; }
    public UUID getAssentoId() { return assentoId; }
    public LocalDateTime getDataHoraApresentacao() { return dataHoraApresentacao; }
    public TipoIngresso getTipo() { return tipo; }
    public StatusIngresso getStatus() { return status; }
    public BigDecimal getValorPago() { return valorPago; }
    public String getCodigoQr() { return codigoQr; }
    public String getCodigoTransacao() { return codigoTransacao; }
    public MetodoPagamento getMetodoPagamento() { return metodoPagamento; }
    public LocalDateTime getDataCompra() { return dataCompra; }
    public BigDecimal getValorReembolsado() { return valorReembolsado; }

    public static class IngressoEvento {
        private final Ingresso ingresso;

        private IngressoEvento(Ingresso ingresso) {
            this.ingresso = ingresso;
        }

        public Ingresso getIngresso() {
            return ingresso;
        }
    }

    public static class CompradoEvento extends IngressoEvento {
        private CompradoEvento(Ingresso ingresso) {
            super(ingresso);
        }
    }

    public static class ReembolsadoEvento extends IngressoEvento {
        private ReembolsadoEvento(Ingresso ingresso) {
            super(ingresso);
        }
    }
}
