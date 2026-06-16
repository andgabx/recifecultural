package recifecultural.apresentacao.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.inteligencia.*;

@RestController
@RequestMapping("/api/inteligencia")
public class InteligenciaControlador {

    private final InteligenciaServicoAplicacao inteligenciaServico;

    @Autowired
    public InteligenciaControlador(InteligenciaServicoAplicacao inteligenciaServico) {
        this.inteligenciaServico = inteligenciaServico;
    }

    @PostMapping("/prever-receita")
    public PrevisaoReceitaResposta preverReceita(@RequestBody PrevisaoReceitaRequisicao req) {
        return inteligenciaServico.preverReceita(req.getOrcamentoMarketing(), req.getPatrocinio());
    }

    @PostMapping("/prever-noshow")
    public PrevisaoNoShowResposta preverNoShow(@RequestBody PrevisaoNoShowRequisicao req) {
        return inteligenciaServico.preverNoShow(
                req.getIngressoId(),
                req.getAntecedenciaCompraDias(),
                req.getPrevisaoClima()
        );
    }
}