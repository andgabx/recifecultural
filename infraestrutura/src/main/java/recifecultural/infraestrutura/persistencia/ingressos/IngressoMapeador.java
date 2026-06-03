package recifecultural.infraestrutura.persistencia.ingressos;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.ingressos.Ingresso;
import recifecultural.dominio.ingressos.IngressoId;
import recifecultural.dominio.ingressos.MetodoPagamento;
import recifecultural.dominio.ingressos.TipoIngresso;

public class IngressoMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<IngressoJpa, Ingresso>() {
            @Override
            protected Ingresso convert(IngressoJpa s) {
                return new Ingresso(
                        new IngressoId(s.id),
                        s.eventoId, s.assentoId, s.dataHoraApresentacao,
                        TipoIngresso.valueOf(s.tipo),
                        s.valorPago, s.codigoQr,
                        s.codigoTransacao,
                        MetodoPagamento.valueOf(s.metodoPagamento),
                        s.dataCompra, s.status, s.valorReembolsado
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Ingresso, IngressoJpa>() {
            @Override
            protected IngressoJpa convert(Ingresso s) {
                var jpa = new IngressoJpa();
                jpa.id = s.getId().valor();
                jpa.eventoId = s.getEventoId();
                jpa.assentoId = s.getAssentoId();
                jpa.dataHoraApresentacao = s.getDataHoraApresentacao();
                jpa.tipo = s.getTipo().name();
                jpa.status = s.getStatus();
                jpa.valorPago = s.getValorPago();
                jpa.codigoQr = s.getCodigoQr();
                jpa.codigoTransacao = s.getCodigoTransacao();
                jpa.metodoPagamento = s.getMetodoPagamento().name();
                jpa.dataCompra = s.getDataCompra();
                jpa.valorReembolsado = s.getValorReembolsado();
                return jpa;
            }
        });
    }
}
