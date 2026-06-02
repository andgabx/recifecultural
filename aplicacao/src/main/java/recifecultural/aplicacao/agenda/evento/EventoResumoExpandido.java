package recifecultural.aplicacao.agenda.evento;

import java.util.List;

public interface EventoResumoExpandido extends EventoResumo {
    String getDescricaoCurta();
    String getDescricaoLonga();
    String getPromotorId();
    String getLocalId();
    String getPeriodoInicio();
    String getPeriodoFim();
    String getPrecoInteira();
    String getPrecoMeia();
    String getPrecoSocial();
    List<ApresentacaoResumo> getApresentacoes();
}
