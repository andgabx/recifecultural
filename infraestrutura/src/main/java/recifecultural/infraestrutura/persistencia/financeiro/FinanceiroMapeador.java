package recifecultural.infraestrutura.persistencia.financeiro;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.financeiro.Despesa;
import recifecultural.dominio.financeiro.DespesaId;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.dominio.financeiro.OrcamentoPeriodo;

public class FinanceiroMapeador {

    public static void registrar(ModelMapper mapper) {
        registrarOrcamento(mapper);
        registrarDespesa(mapper);
    }

    private static void registrarOrcamento(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<OrcamentoPeriodoJpa, OrcamentoPeriodo>() {
            @Override
            protected OrcamentoPeriodo convert(OrcamentoPeriodoJpa s) {
                return new OrcamentoPeriodo(
                        new OrcamentoId(s.id),
                        new recifecultural.dominio.financeiro.Periodo(s.periodoInicio, s.periodoFim),
                        s.valorTotal,
                        s.status
                );
            }
        });

        mapper.addConverter(new AbstractConverter<OrcamentoPeriodo, OrcamentoPeriodoJpa>() {
            @Override
            protected OrcamentoPeriodoJpa convert(OrcamentoPeriodo s) {
                var jpa = new OrcamentoPeriodoJpa();
                jpa.id = s.getId().valor();
                jpa.periodoInicio = s.getPeriodo().getDataInicio();
                jpa.periodoFim = s.getPeriodo().getDataFim();
                jpa.valorTotal = s.getValorTotal();
                jpa.status = s.getStatus();
                return jpa;
            }
        });
    }

    private static void registrarDespesa(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<DespesaJpa, Despesa>() {
            @Override
            protected Despesa convert(DespesaJpa s) {
                return new Despesa(
                        new DespesaId(s.id),
                        new OrcamentoId(s.orcamentoId),
                        s.descricao, s.valor, s.categoria
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Despesa, DespesaJpa>() {
            @Override
            protected DespesaJpa convert(Despesa s) {
                var jpa = new DespesaJpa();
                jpa.id = s.getId().valor();
                jpa.orcamentoId = s.getOrcamentoId().valor();
                jpa.descricao = s.getDescricao();
                jpa.valor = s.getValor();
                jpa.categoria = s.getCategoria();
                jpa.dataRegistro = s.getDataRegistro();
                return jpa;
            }
        });
    }
}
