package recifecultural.aplicacao.patrocinio;

import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.ModalidadeContribuicao;
import recifecultural.dominio.patrocinio.PatrocinioId;
import recifecultural.dominio.patrocinio.PatrocinioServico;
import recifecultural.dominio.patrocinio.ResultadoCancelamento;
import recifecultural.dominio.patrocinio.ResultadoSubsidio;
import recifecultural.dominio.patrocinio.TipoPatrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.apache.commons.lang3.Validate.notNull;

public class PatrocinioServicoAplicacao {

    private final PatrocinioServico servico;
    private final PatrocinioRepositorioAplicacao repositorio;

    public PatrocinioServicoAplicacao(PatrocinioServico servico, PatrocinioRepositorioAplicacao repositorio) {
        notNull(servico, "PatrocinioServico não pode ser nulo.");
        notNull(repositorio, "PatrocinioRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<PatrocinioResumo> pesquisarPorEvento(UUID eventoId) {
        return repositorio.pesquisarPorEvento(eventoId);
    }

    public PatrocinioId criar(EventoId eventoId, String patrocinadorNome, String categoriaPatrocinio,
                              TipoPatrocinio tipo, ModalidadeContribuicao modalidade,
                              BigDecimal valorContribuicao, LocalDateTime dataEvento, boolean eventoAprovado) {
        return servico.criar(eventoId, patrocinadorNome, categoriaPatrocinio, tipo,
                modalidade, valorContribuicao, dataEvento, eventoAprovado).getId();
    }

    public void ativar(PatrocinioId id) {
        servico.ativar(id);
    }

    public ResultadoCancelamento cancelarPorEvento(PatrocinioId id, LocalDateTime agora) {
        return servico.cancelarPorEvento(id, agora);
    }

    public ResultadoCancelamento cancelarPorPatrocinador(PatrocinioId id, LocalDateTime agora) {
        return servico.cancelarPorPatrocinador(id, agora);
    }

    public ResultadoSubsidio calcularSubsidio(PatrocinioId id, BigDecimal precoSocialAtual) {
        return servico.calcularSubsidio(id, precoSocialAtual);
    }
}
