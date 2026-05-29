package recifecultural.aplicacao.financeiro;

import recifecultural.dominio.financeiro.CategoriaDespesa;
import recifecultural.dominio.financeiro.DesempenhoTeatroServico;
import recifecultural.dominio.financeiro.OrcamentoId;
import recifecultural.dominio.financeiro.ResultadoRegistroDespesa;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.apache.commons.lang3.Validate.notNull;

public class FinanceiroServicoAplicacao {

    private final DesempenhoTeatroServico servico;
    private final FinanceiroRepositorioAplicacao repositorio;

    public FinanceiroServicoAplicacao(DesempenhoTeatroServico servico, FinanceiroRepositorioAplicacao repositorio) {
        notNull(servico, "DesempenhoTeatroServico não pode ser nulo.");
        notNull(repositorio, "FinanceiroRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public IndicadoresResumo buscarIndicadores(LocalDate inicio, LocalDate fim, int capacidadeTotal) {
        return repositorio.buscarIndicadores(inicio, fim, capacidadeTotal);
    }

    public ResultadoRegistroDespesa registrarDespesa(OrcamentoId orcamentoId, String descricao,
                                                      BigDecimal valor, CategoriaDespesa categoria) {
        return servico.registrarDespesa(orcamentoId, descricao, valor, categoria);
    }
}
