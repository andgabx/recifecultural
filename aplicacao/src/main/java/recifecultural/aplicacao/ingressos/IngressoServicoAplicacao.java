package recifecultural.aplicacao.ingressos;

import recifecultural.dominio.cupom.AplicarCupomServico;
import recifecultural.dominio.ingressos.IConfirmacaoReserva;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.IngressoServico;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.ResultadoReembolso;
import recifecultural.dominio.ingressos.TipoIngresso;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class IngressoServicoAplicacao {

    /** Representa um item em uma compra múltipla com pré-reserva. */
    public record ItemCompraMultipla(
            UUID preReservaId,
            UUID assentoId,
            TipoIngresso tipo,
            BigDecimal valor) {}

    private final IngressoServico servico;
    private final IngressoRepositorioAplicacao repositorio;
    private final AplicarCupomServico cupomServico;

    public IngressoServicoAplicacao(IngressoServico servico,
                                    IngressoRepositorioAplicacao repositorio,
                                    AplicarCupomServico cupomServico) {
        notNull(servico, "IngressoServico não pode ser nulo.");
        notNull(repositorio, "IngressoRepositorioAplicacao não pode ser nulo.");
        notNull(cupomServico, "AplicarCupomServico não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
        this.cupomServico = cupomServico;
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

    /**
     * Compra múltiplos ingressos com pré-reserva, aplicando desconto de cupom de forma
     * proporcional quando um cupom for fornecido. A lógica de cálculo do desconto fica
     * inteiramente nesta camada de aplicação, mantendo o controlador livre de regras de negócio.
     *
     * @param eventoId            identificador do evento
     * @param dataHora            data e hora da apresentação
     * @param itens               lista de itens a comprar
     * @param metodoPagamento     método de pagamento
     * @param capacidadeMaxima    capacidade máxima do evento
     * @param codigoCupom         código do cupom (pode ser null/blank para indicar ausência)
     * @param cpfComprador        CPF do comprador (usado na validação do cupom)
     * @param categoriaEvento     categoria do evento (usada na validação do cupom)
     * @param confirmacaoReserva  callback para confirmar ou cancelar pré-reservas
     * @return lista de IDs dos ingressos criados
     */
    public List<IngressoId> comprarMultiplosComCupom(
            UUID eventoId,
            LocalDateTime dataHora,
            List<ItemCompraMultipla> itens,
            MetodoPagamento metodoPagamento,
            int capacidadeMaxima,
            String codigoCupom,
            String cpfComprador,
            String categoriaEvento,
            IConfirmacaoReserva confirmacaoReserva) {

        boolean temCupom = codigoCupom != null && !codigoCupom.isBlank()
                && cpfComprador != null && !cpfComprador.isBlank()
                && categoriaEvento != null && !categoriaEvento.isBlank();

        List<BigDecimal> valoresFinal = new ArrayList<>();
        if (temCupom) {
            BigDecimal totalBruto = itens.stream()
                    .map(ItemCompraMultipla::valor)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // preview não consome o cupom; o consumo ocorre abaixo via aplicarDesconto
            var preview = cupomServico.previewDesconto(codigoCupom, cpfComprador, totalBruto, categoriaEvento);
            BigDecimal totalComDesconto = preview.valorFinal();
            BigDecimal fator = totalBruto.compareTo(BigDecimal.ZERO) == 0
                    ? BigDecimal.ONE
                    : totalComDesconto.divide(totalBruto, 10, RoundingMode.HALF_UP);

            for (ItemCompraMultipla item : itens) {
                valoresFinal.add(item.valor().multiply(fator).setScale(2, RoundingMode.HALF_UP));
            }
            // Registra o uso do cupom uma única vez
            cupomServico.aplicarDesconto(codigoCupom, cpfComprador, totalBruto, categoriaEvento);
        } else {
            itens.forEach(item -> valoresFinal.add(item.valor()));
        }

        List<IngressoId> ids = new ArrayList<>();
        for (int i = 0; i < itens.size(); i++) {
            ItemCompraMultipla item = itens.get(i);
            IngressoId id = servico.comprarComPreReserva(
                    eventoId, dataHora,
                    item.preReservaId(), item.assentoId(),
                    item.tipo(), valoresFinal.get(i),
                    metodoPagamento, capacidadeMaxima,
                    confirmacaoReserva).getId();
            ids.add(id);
        }
        return ids;
    }

    public ResultadoReembolso solicitarReembolso(IngressoId id, LocalDateTime agora) {
        return servico.solicitarReembolso(id, agora);
    }
}
