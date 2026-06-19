package recifecultural.infraestrutura.persistencia.ingressos;

import org.springframework.stereotype.Repository;

import recifecultural.aplicacao.ingressos.IngressoRepositorioAplicacao;
import recifecultural.aplicacao.ingressos.IngressoResumo;
import recifecultural.dominio.ingressos.IIngressoRepositorio;
import recifecultural.dominio.ingressos.Ingresso;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.infraestrutura.persistencia.jpa.JpaMapeador;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public class IngressoRepositorioImpl implements IIngressoRepositorio, IngressoRepositorioAplicacao {

    private final IngressoJpaRepository jpa;
    private final JpaMapeador mapeador;

    public IngressoRepositorioImpl(IngressoJpaRepository jpa, JpaMapeador mapeador) {
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
    public Set<UUID> buscarAssentosOcupadosPorEvento(UUID eventoId) {
        List<UUID> result = jpa.findAssentosOcupadosByEventoId(eventoId);
        return new HashSet<>(result);
    }

    @Override
    public List<IngressoResumo> pesquisarPorEvento(UUID eventoId) {
        return jpa.findByEventoId(eventoId).stream()
                .<IngressoResumo>map(this::toResumo)
                .toList();
    }

    @Override
    public List<IngressoResumo> listarTodos() {
        return jpa.findAll().stream()
                .<IngressoResumo>map(this::toResumo)
                .toList();
    }

    private IngressoResumo toResumo(IngressoJpa i) {
        return new IngressoResumoJpa(
                i.id.toString(), i.eventoId.toString(),
                i.tipo, i.status != null ? i.status.name() : null,
                i.dataHoraApresentacao != null ? i.dataHoraApresentacao.toString() : null,
                i.dataCompra != null ? i.dataCompra.toString() : null,
                i.metodoPagamento,
                i.valorPago != null ? i.valorPago.toPlainString() : null,
                i.codigoQr);
    }

    record IngressoResumoJpa(String id, String eventoId, String tipo, String status,
                             String dataHoraApresentacao, String dataCompra,
                             String metodoPagamento, String valorPago, String codigoQr)
            implements IngressoResumo {
        public String getId() { return id; }
        public String getEventoId() { return eventoId; }
        public String getTipo() { return tipo; }
        public String getStatus() { return status; }
        public String getDataHoraApresentacao() { return dataHoraApresentacao; }
        public String getDataCompra() { return dataCompra; }
        public String getMetodoPagamento() { return metodoPagamento; }
        public String getValorPago() { return valorPago; }
        public String getCodigoQr() { return codigoQr; }
    }
}
