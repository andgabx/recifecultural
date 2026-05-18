package recifecultural.infraestrutura.persistencia.jpa;

import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.ingressos.IngressoRepositorioAplicacao;
import recifecultural.aplicacao.ingressos.IngressoResumo;
import recifecultural.dominio.ingressos.IIngressoRepositorio;
import recifecultural.dominio.ingressos.Ingresso;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.StatusIngresso;
import recifecultural.dominio.ingressos.TipoIngresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ingresso")
class IngressoJpa {
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
}

interface IngressoJpaRepository extends JpaRepository<IngressoJpa, UUID> {
    IngressoJpa findByCodigoQr(String codigoQr);

    @Query("SELECT COUNT(i) FROM IngressoJpa i WHERE i.eventoId = :eventoId AND i.dataHoraApresentacao = :dataHora AND i.status = 'ATIVO'")
    int countAtivosPorApresentacao(UUID eventoId, LocalDateTime dataHora);

    @Query("SELECT i FROM IngressoJpa i WHERE i.dataHoraApresentacao >= :inicio AND i.dataHoraApresentacao <= :fim")
    List<IngressoJpa> findByPeriodo(LocalDateTime inicio, LocalDateTime fim);

    List<IngressoJpa> findByEventoId(UUID eventoId);
}

@Repository
class IngressoRepositorioImpl implements IIngressoRepositorio, IngressoRepositorioAplicacao {

    private final IngressoJpaRepository jpa;
    private final JpaMapeador mapeador;

    IngressoRepositorioImpl(IngressoJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public void salvar(Ingresso ingresso) {
        jpa.save(mapeador.map(ingresso, IngressoJpa.class));
    }

    @Override
    public Ingresso buscarPorId(IngressoId id) {
        return jpa.findById(id.valor())
                .map(i -> mapeador.map(i, Ingresso.class))
                .orElse(null);
    }

    @Override
    public Ingresso buscarPorCodigoQr(String codigoQr) {
        var jpaObj = jpa.findByCodigoQr(codigoQr);
        return jpaObj != null ? mapeador.map(jpaObj, Ingresso.class) : null;
    }

    @Override
    public int contarAtivosPorApresentacao(UUID eventoId, LocalDateTime dataHora) {
        return jpa.countAtivosPorApresentacao(eventoId, dataHora);
    }

    @Override
    public List<Ingresso> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return jpa.findByPeriodo(inicio, fim)
                .stream().map(i -> mapeador.map(i, Ingresso.class)).toList();
    }

    @Override
    public List<IngressoResumo> pesquisarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .<IngressoResumo>map(i -> new IngressoResumoJpa(
                        i.id.toString(), i.eventoId.toString(),
                        i.tipo, i.status != null ? i.status.name() : null,
                        i.dataHoraApresentacao != null ? i.dataHoraApresentacao.toString() : null))
                .toList();
    }

    record IngressoResumoJpa(String id, String eventoId, String tipo, String status, String dataHoraApresentacao)
            implements IngressoResumo {
        public String getId() { return id; }
        public String getEventoId() { return eventoId; }
        public String getTipo() { return tipo; }
        public String getStatus() { return status; }
        public String getDataHoraApresentacao() { return dataHoraApresentacao; }
    }
}
