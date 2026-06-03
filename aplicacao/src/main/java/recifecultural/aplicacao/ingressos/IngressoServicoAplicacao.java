package recifecultural.aplicacao.ingressos;

import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.IngressoServico;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.ResultadoReembolso;
import recifecultural.dominio.ingressos.TipoIngresso;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class IngressoServicoAplicacao {

    private final IngressoServico servico;
    private final IngressoRepositorioAplicacao repositorio;

    public IngressoServicoAplicacao(IngressoServico servico, IngressoRepositorioAplicacao repositorio) {
        notNull(servico, "IngressoServico não pode ser nulo.");
        notNull(repositorio, "IngressoRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<IngressoResumo> pesquisarPorEvento(UUID eventoId) {
        return repositorio.pesquisarPorEvento(eventoId);
    }

    public List<IngressoResumo> listarTodos() {
        return repositorio.listarTodos();
    }

    public IngressoId comprar(UUID eventoId, LocalDateTime dataHora, TipoIngresso tipo,
                              BigDecimal valor, MetodoPagamento metodo, int capacidadeMaxima) {
        return servico.comprar(eventoId, dataHora, tipo, valor, metodo, capacidadeMaxima).getId();
    }

    public IngressoId comprarComCupom(UUID eventoId, LocalDateTime dataHora, TipoIngresso tipo,
                                      BigDecimal valor, MetodoPagamento metodo, int capacidadeMaxima,
                                      String codigoCupom, String cpfComprador, String categoriaEvento) {
        return servico.comprarComCupom(eventoId, dataHora, tipo, valor, metodo, capacidadeMaxima,
                codigoCupom, cpfComprador, categoriaEvento).getId();
    }

    public IngressoId comprarComPreReserva(UUID eventoId, LocalDateTime dataHora,
                                            UUID preReservaId, UUID assentoId,
                                            TipoIngresso tipo, BigDecimal valor,
                                            MetodoPagamento metodo, int capacidadeMaxima,
                                            recifecultural.dominio.ingressos.IConfirmacaoReserva confirmacaoReserva) {
        return servico.comprarComPreReserva(eventoId, dataHora, preReservaId, assentoId,
                tipo, valor, metodo, capacidadeMaxima, confirmacaoReserva).getId();
    }

    public IngressoId comprarComPreReservaComCupom(UUID eventoId, LocalDateTime dataHora,
                                                    UUID preReservaId, UUID assentoId,
                                                    TipoIngresso tipo, BigDecimal valor,
                                                    MetodoPagamento metodo, int capacidadeMaxima,
                                                    String codigoCupom, String cpfComprador,
                                                    String categoriaEvento,
                                                    recifecultural.dominio.ingressos.IConfirmacaoReserva confirmacaoReserva) {
        return servico.comprarComPreReservaComCupom(eventoId, dataHora, preReservaId, assentoId,
                tipo, valor, metodo, capacidadeMaxima,
                codigoCupom, cpfComprador, categoriaEvento,
                confirmacaoReserva).getId();
    }

    public ResultadoReembolso solicitarReembolso(IngressoId id, LocalDateTime agora) {
        return servico.solicitarReembolso(id, agora);
    }
}
