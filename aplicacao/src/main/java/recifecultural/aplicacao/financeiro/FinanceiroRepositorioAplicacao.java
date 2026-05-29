package recifecultural.aplicacao.financeiro;

import java.time.LocalDate;

public interface FinanceiroRepositorioAplicacao {
    IndicadoresResumo buscarIndicadores(LocalDate inicio, LocalDate fim, int capacidadeTotal);
}
