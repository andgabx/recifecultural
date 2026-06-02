package recifecultural.apresentacao.bff.checkout;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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

record CompraComPreReservaRequisicao(
        UUID eventoId,
        LocalDateTime dataHoraApresentacao,
        UUID preReservaId,
        UUID assentoId,
        String tipo,
        BigDecimal valor,
        String metodoPagamento,
        int capacidadeMaxima) {}

record CompraMultiplaRequisicao(
        UUID eventoId,
        LocalDateTime dataHoraApresentacao,
        String metodoPagamento,
        int capacidadeMaxima,
        List<ItemCompra> itens,
        String codigoCupom,
        String cpfComprador,
        String categoriaEvento) {

    record ItemCompra(UUID preReservaId, UUID assentoId, String tipo, BigDecimal valor) {}
}
