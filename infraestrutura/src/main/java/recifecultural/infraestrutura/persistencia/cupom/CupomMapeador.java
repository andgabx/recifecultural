package recifecultural.infraestrutura.persistencia.cupom;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.cupom.Cupom;
import recifecultural.dominio.cupom.CupomId;

public class CupomMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<CupomJpa, Cupom>() {
            @Override
            protected Cupom convert(CupomJpa s) {
                return new Cupom(
                        new CupomId(s.id), s.codigo, s.tipoDesconto,
                        s.valorDesconto, s.valorMinimoPedido,
                        s.limiteGlobal, s.limitePorCpf,
                        s.dataInicio, s.dataFim, s.categoriaPermitida,
                        s.usosGlobais, s.cpfsQueJaUsaram
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Cupom, CupomJpa>() {
            @Override
            protected CupomJpa convert(Cupom s) {
                var jpa = new CupomJpa();
                jpa.id = s.getId().getValor();
                jpa.codigo = s.getCodigo();
                jpa.tipoDesconto = s.getTipoDesconto();
                jpa.valorDesconto = s.getValorDesconto();
                jpa.valorMinimoPedido = s.getValorMinimoPedido();
                jpa.limiteGlobal = s.getLimiteGlobal();
                jpa.usosGlobais = s.getUsosGlobais();
                jpa.limitePorCpf = s.getLimitePorCpf();
                jpa.dataInicio = s.getDataInicio();
                jpa.dataFim = s.getDataFim();
                jpa.categoriaPermitida = s.getCategoriaPermitida();
                jpa.cpfsQueJaUsaram = new java.util.HashSet<>(s.getCpfsQueJaUsaram());
                return jpa;
            }
        });
    }
}
