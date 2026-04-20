package recifecultural.dominio.agenda;

import lombok.Getter;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Getter
public class Evento {

    public enum StatusEvento {
        AGENDADO, CANCELADO, REALIZADO
    }

    private final UUID id;
    private final UUID promotorId;
    private final UUID localId;

    private String titulo;
    private String descricaoCurta;
    private String descricaoLonga;

    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private final List<LocalDateTime> datasApresentacao;

    private URI enderecoIngresso;

    private BigDecimal precoInteira;
    private BigDecimal precoMeia;
    private String precoSocial;

    private StatusEvento status;

    public Evento(
            UUID promotorId,
            UUID localId,
            String titulo,
            String descricaoCurta,
            String descricaoLonga,
            LocalDateTime dataInicio,
            LocalDateTime dataFim,
            URI enderecoIngresso,
            BigDecimal precoInteira,
            BigDecimal precoMeia,
            String precoSocial
    ) {
        this.id = UUID.randomUUID();
        this.promotorId = promotorId;
        this.localId = localId;

        setTitulo(titulo);
        this.descricaoCurta = descricaoCurta;
        this.descricaoLonga = descricaoLonga;

        validarPeriodo(dataInicio, dataFim);
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;

        this.datasApresentacao = new ArrayList<>();
        this.enderecoIngresso = enderecoIngresso;

        validarPrecos(precoInteira, precoMeia);
        this.precoInteira = precoInteira;
        this.precoMeia = precoMeia;
        this.precoSocial = precoSocial;
        this.status = StatusEvento.AGENDADO;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.isBlank()) {
            throw new IllegalArgumentException("Título é obrigatório.");
        }
        this.titulo = titulo;
    }

    public void programarApresentacao(LocalDateTime dataHora) {
        if (dataHora == null) throw new IllegalArgumentException("Data nula.");
        if (dataHora.isBefore(dataInicio) || dataHora.isAfter(dataFim)) {
            throw new IllegalArgumentException("Apresentação fora do período do evento.");
        }
        this.datasApresentacao.add(dataHora);
    }

    public void cancelar(String motivoCancelamento) {
        if (this.status == StatusEvento.CANCELADO) {
            return;
        }
        this.status = StatusEvento.CANCELADO;
    }

    private void validarPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio == null || fim == null) throw new IllegalArgumentException("Datas são obrigatórias.");
        if (fim.isBefore(inicio)) throw new IllegalArgumentException("Fim antes do início.");
    }

    private void validarPrecos(BigDecimal inteira, BigDecimal meia) {
        if (inteira != null && inteira.signum() < 0) throw new IllegalArgumentException("Preço negativo.");
        if (meia != null && inteira != null && meia.compareTo(inteira) > 0) {
            throw new IllegalArgumentException("Meia entrada não pode ser maior que a inteira.");
        }
    }

    public List<LocalDateTime> getDatasApresentacao() {
        return Collections.unmodifiableList(datasApresentacao);
    }
}