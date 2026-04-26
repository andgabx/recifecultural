package recifecultural.dominio.ingressos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notNull;

public class IngressoServico {

    private final IIngressoRepositorio repositorio;
    private final IGatewayPagamento gateway;

    public IngressoServico(IIngressoRepositorio repositorio, IGatewayPagamento gateway) {
        notNull(repositorio, "O repositório de ingressos não pode ser nulo.");
        notNull(gateway, "O gateway de pagamento não pode ser nulo.");
        this.repositorio = repositorio;
        this.gateway = gateway;
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
        return ingresso;
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

        ingresso.reembolsar(resultado.getValorReembolsado());
        repositorio.salvar(ingresso);
        return resultado;
    }
}
