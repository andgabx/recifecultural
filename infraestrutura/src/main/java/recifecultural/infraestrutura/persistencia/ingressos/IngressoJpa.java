package recifecultural.infraestrutura.persistencia.ingressos;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import recifecultural.dominio.ingressos.StatusIngresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ingresso")
public class IngressoJpa {
    @Id
    UUID id;
    UUID eventoId;
    UUID assentoId;
    LocalDateTime dataHoraApresentacao;
    String tipo;
    @Enumerated(EnumType.STRING)
    StatusIngresso status;
    BigDecimal valorPago;
    String codigoQr;
    String codigoTransacao;
    String metodoPagamento;
    LocalDateTime dataCompra;
    BigDecimal valorReembolsado;

    public BigDecimal getValorPago() { return valorPago; }
    public BigDecimal getValorReembolsado() { return valorReembolsado; }
}

