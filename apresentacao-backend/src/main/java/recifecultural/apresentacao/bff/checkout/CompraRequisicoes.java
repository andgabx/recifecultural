package recifecultural.apresentacao.bff.checkout;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

record CompraRequisicao(
        UUID eventoId,
        LocalDateTime dataHoraApresentacao,
        String tipo,
        BigDecimal valor,
        String metodoPagamento,
        int capacidadeMaxima) {}

record CompraComCupomRequisicao(
        UUID eventoId,
        LocalDateTime dataHoraApresentacao,
        String tipo,
        BigDecimal valor,
        String metodoPagamento,
        int capacidadeMaxima,
        String codigoCupom,
        String cpfComprador,
        String categoriaEvento) {}
