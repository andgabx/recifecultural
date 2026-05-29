package recifecultural.infraestrutura.padroes;

import recifecultural.dominio.ingressos.MetodoPagamento;

import java.util.List;

public class SeletorEstrategiaReembolso {

    private static final List<EstrategiaProcessamentoReembolso> ESTRATEGIAS = List.of(
            new EstrategiaReembolsoImediato(),
            new EstrategiaReembolsoBancario()
    );

    private static final EstrategiaProcessamentoReembolso PADRAO = new EstrategiaReembolsoBancario();

    public EstrategiaProcessamentoReembolso selecionar(MetodoPagamento metodo) {
        return ESTRATEGIAS.stream()
                .filter(e -> e.aplicavelA(metodo))
                .findFirst()
                .orElse(PADRAO);
    }
}
