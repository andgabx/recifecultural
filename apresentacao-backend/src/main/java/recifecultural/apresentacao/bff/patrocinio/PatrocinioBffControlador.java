package recifecultural.apresentacao.bff.patrocinio;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import recifecultural.aplicacao.patrocinio.PatrocinioResumo;
import recifecultural.aplicacao.patrocinio.PatrocinioServicoAplicacao;
import recifecultural.apresentacao.bff.AbstractBffControlador;
import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.ModalidadeContribuicao;
import recifecultural.dominio.patrocinio.PatrocinioId;
import recifecultural.dominio.patrocinio.ResultadoCancelamento;
import recifecultural.dominio.patrocinio.ResultadoSubsidio;
import recifecultural.dominio.patrocinio.TipoPatrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Tag(name = "BFF — Patrocínio")
@RestController
@RequestMapping("/api/bff/patrocinios")
public class PatrocinioBffControlador extends AbstractBffControlador {

    private final PatrocinioServicoAplicacao servico;

    public PatrocinioBffControlador(PatrocinioServicoAplicacao servico) {
        this.servico = servico;
    }

    @Operation(summary = "Lista patrocínios do evento (Proxy serve do cache)")
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<PatrocinioResumo>> listarPorEvento(@PathVariable UUID eventoId) {
        return responder(servico.pesquisarPorEvento(eventoId));
    }

    @Operation(summary = "Cria patrocínio")
    @PostMapping
    public ResponseEntity<Map<String, String>> criar(@RequestBody CriarPatrocinioRequisicao req) {
        PatrocinioId id = servico.criar(
                new EventoId(req.eventoId()),
                req.patrocinadorNome(), req.categoriaPatrocinio(),
                TipoPatrocinio.valueOf(req.tipo()),
                ModalidadeContribuicao.valueOf(req.modalidade()),
                req.valorContribuicao(), req.dataEvento(),
                req.eventoAprovado());
        return responderCriado(id.getValor().toString());
    }

    @Operation(summary = "Ativa patrocínio")
    @PostMapping("/{id}/ativar")
    public ResponseEntity<Map<String, String>> ativar(@PathVariable UUID id) {
        servico.ativar(new PatrocinioId(id));
        return responderSemConteudo();
    }

    @Operation(summary = "Cancela patrocínio por evento + retorna simulação")
    @PostMapping("/{id}/cancelar-por-evento")
    public ResponseEntity<SimulacaoCancelamentoPatrocinio> cancelarPorEvento(@PathVariable UUID id) {
        ResultadoCancelamento resultado = servico.cancelarPorEvento(new PatrocinioId(id), LocalDateTime.now());
        return responder(new SimulacaoCancelamentoPatrocinio(resultado.getValorReembolsado(), resultado.getMultaAplicada()));
    }

    @Operation(summary = "Cancela patrocínio por patrocinador + retorna simulação")
    @PostMapping("/{id}/cancelar-por-patrocinador")
    public ResponseEntity<SimulacaoCancelamentoPatrocinio> cancelarPorPatrocinador(@PathVariable UUID id) {
        ResultadoCancelamento resultado = servico.cancelarPorPatrocinador(new PatrocinioId(id), LocalDateTime.now());
        return responder(new SimulacaoCancelamentoPatrocinio(resultado.getValorReembolsado(), resultado.getMultaAplicada()));
    }

    @Operation(summary = "Calcula subsídio social do patrocínio")
    @GetMapping("/{id}/subsidio")
    public ResponseEntity<SimulacaoSubsidio> calcularSubsidio(
            @PathVariable UUID id,
            @RequestParam BigDecimal precoSocial) {
        ResultadoSubsidio resultado = servico.calcularSubsidio(new PatrocinioId(id), precoSocial);
        return responder(new SimulacaoSubsidio(resultado.getNovoPrecoSocial(), resultado.isPisoAplicado()));
    }
}
