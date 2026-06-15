package recifecultural.dominio.ingressos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.cupom.AplicarCupomServico;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class IngressoServico {

    private final IIngressoRepositorio repositorio;
    private final IGatewayPagamento gateway;
    private final EventoBarramento barramento;
    private final AplicarCupomServico cupomServico;

    public IngressoServico(IIngressoRepositorio repositorio, IGatewayPagamento gateway) {
        this(repositorio, gateway, null, null);
    }

    public IngressoServico(IIngressoRepositorio repositorio, IGatewayPagamento gateway, EventoBarramento barramento) {
        this(repositorio, gateway, barramento, null);
    }

    public IngressoServico(IIngressoRepositorio repositorio,
                           IGatewayPagamento gateway,
                           EventoBarramento barramento,
                           AplicarCupomServico cupomServico) {
        notNull(repositorio, "O repositório de ingressos não pode ser nulo.");
        notNull(gateway, "O gateway de pagamento não pode ser nulo.");
        this.repositorio = repositorio;
        this.gateway = gateway;
        this.barramento = barramento;
        this.cupomServico = cupomServico;
    }


    public Ingresso comprar(UUID eventoId,
                            LocalDateTime dataHora,
                            TipoIngresso tipo,
                            BigDecimal valor,
                            MetodoPagamento metodo,
                            int capacidadeMaxima) {
        notNull(eventoId, "O id do evento não pode ser nulo.");
        notNull(dataHora, "A data e hora da apresentação não podem ser nulas.");
        notNull(tipo, "O tipo do ingresso não pode ser nulo.");
        notNull(valor, "O valor não pode ser nulo.");
        notNull(metodo, "O método de pagamento não pode ser nulo.");

        int ativos = repositorio.contarAtivosPorApresentacao(eventoId, dataHora);
        isTrue(ativos < capacidadeMaxima, "Capacidade esgotada para esta apresentação.");

        IngressoId ingressoId = IngressoId.novo();
        ResultadoPagamento resultado = gateway.processar(ingressoId, valor, metodo);
        isTrue(resultado.isAprovado(), "Pagamento recusado pelo gateway.");

        String codigoQr = UUID.randomUUID().toString();

        Ingresso ingresso = new Ingresso(
                ingressoId,
                eventoId,
                dataHora,
                tipo,
                valor,
                codigoQr,
                resultado.getCodigoTransacao(),
                metodo
        );

        repositorio.salvar(ingresso);
        postar(ingresso.eventoCompra());
        return ingresso;
    }

    public Ingresso comprarComCupom(UUID eventoId,
                                    LocalDateTime dataHora,
                                    TipoIngresso tipo,
                                    BigDecimal valor,
                                    MetodoPagamento metodo,
                                    int capacidadeMaxima,
                                    String codigoCupom,
                                    String cpfComprador,
                                    String categoriaEvento) {
        notNull(cupomServico, "Serviço de cupom não está configurado neste IngressoServico.");
        notBlank(codigoCupom, "O código do cupom é obrigatório.");
        notBlank(cpfComprador, "O CPF do comprador é obrigatório.");
        notBlank(categoriaEvento, "A categoria do evento é obrigatória.");
        notNull(valor, "O valor não pode ser nulo.");

        BigDecimal valorComDesconto = cupomServico.aplicarDesconto(codigoCupom, cpfComprador, valor, categoriaEvento);

        return comprar(eventoId, dataHora, tipo, valorComDesconto, metodo, capacidadeMaxima);
    }

    public Ingresso comprarComPreReserva(UUID eventoId,
                                         LocalDateTime dataHora,
                                         UUID preReservaId,
                                         UUID assentoId,
                                         TipoIngresso tipo,
                                         BigDecimal valor,
                                         MetodoPagamento metodo,
                                         int capacidadeMaxima,
                                         IConfirmacaoReserva confirmacaoReserva) {
        notNull(preReservaId, "O id da pré-reserva não pode ser nulo.");
        notNull(assentoId, "O id do assento não pode ser nulo.");
        notNull(confirmacaoReserva, "O serviço de confirmação de reserva é obrigatório.");
        notNull(eventoId, "O id do evento não pode ser nulo.");
        notNull(dataHora, "A data e hora da apresentação não podem ser nulas.");
        notNull(tipo, "O tipo do ingresso não pode ser nulo.");
        notNull(valor, "O valor não pode ser nulo.");
        notNull(metodo, "O método de pagamento não pode ser nulo.");

        int ativos = repositorio.contarAtivosPorApresentacao(eventoId, dataHora);
        isTrue(ativos < capacidadeMaxima, "Capacidade esgotada para esta apresentação.");

        IngressoId ingressoId = IngressoId.novo();
        ResultadoPagamento resultado = gateway.processar(ingressoId, valor, metodo);

        if (!resultado.isAprovado()) {
            confirmacaoReserva.cancelar(preReservaId);
            throw new IllegalStateException("Pagamento recusado pelo gateway.");
        }

        String codigoQr = UUID.randomUUID().toString();

        Ingresso ingresso = new Ingresso(
                ingressoId,
                eventoId,
                assentoId,
                dataHora,
                tipo,
                valor,
                codigoQr,
                resultado.getCodigoTransacao(),
                metodo
        );

        confirmacaoReserva.confirmar(preReservaId);
        repositorio.salvar(ingresso);
        postar(ingresso.eventoCompra());
        return ingresso;
    }

    public Ingresso comprarComPreReservaComCupom(UUID eventoId, LocalDateTime dataHora,
                                                   UUID preReservaId, UUID assentoId,
                                                   TipoIngresso tipo, BigDecimal valor,
                                                   MetodoPagamento metodo, int capacidadeMaxima,
                                                   String codigoCupom, String cpfComprador,
                                                   String categoriaEvento,
                                                   IConfirmacaoReserva confirmacaoReserva) {
        notNull(cupomServico, "Serviço de cupom não configurado.");
        BigDecimal valorComDesconto = cupomServico.aplicarDesconto(codigoCupom, cpfComprador, valor, categoriaEvento);
        return comprarComPreReserva(eventoId, dataHora, preReservaId, assentoId,
                tipo, valorComDesconto, metodo, capacidadeMaxima, confirmacaoReserva);
    }

    public ResultadoReembolso solicitarReembolso(IngressoId id, LocalDateTime agora) {
        notNull(id, "O id do ingresso não pode ser nulo.");
        notNull(agora, "A data/hora atual não pode ser nula.");

        Ingresso ingresso = repositorio.buscarPorId(id);
        notNull(ingresso, "Ingresso não encontrado com id: " + id);

        isTrue(ingresso.getStatus() == StatusIngresso.ATIVO,
                "Apenas ingressos com status ATIVO podem ser reembolsados.");

        BigDecimal valorReembolso = ingresso.calcularReembolso(agora);
        isTrue(valorReembolso.compareTo(BigDecimal.ZERO) > 0,
                "Reembolso não permitido com menos de 2 dias de antecedência.");

        ResultadoReembolso resultado = gateway.reembolsar(
                ingresso.getCodigoTransacao(),
                valorReembolso,
                ingresso.getMetodoPagamento()
        );

        Ingresso.ReembolsadoEvento evento = ingresso.reembolsar(resultado.getValorReembolsado());
        repositorio.salvar(ingresso);
        postar(evento);

        return resultado;
    }

    private <E> void postar(E evento) {
        if (barramento != null) {
            barramento.postar(evento);
        }
    }
}
