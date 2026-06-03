package recifecultural.aplicacao.patrocinio;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.ModalidadeContribuicao;
import recifecultural.dominio.patrocinio.Patrocinio;
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
    private final IEventoRepositorio eventoRepositorio;

    public PatrocinioServicoAplicacao(PatrocinioServico servico,
                                       PatrocinioRepositorioAplicacao repositorio,
                                       IEventoRepositorio eventoRepositorio) {
        notNull(servico, "PatrocinioServico não pode ser nulo.");
        notNull(repositorio, "PatrocinioRepositorioAplicacao não pode ser nulo.");
        notNull(eventoRepositorio, "IEventoRepositorio não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
        this.eventoRepositorio = eventoRepositorio;
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

    /**
     * Ativa o patrocínio. Se a modalidade for SUBSIDIO_INGRESSO_SOCIAL,
     * aplica o desconto no preço social do evento automaticamente.
     */
    public ResultadoSubsidio ativar(PatrocinioId id) {
        servico.ativar(id);

        Patrocinio patrocinio = servico.obterPorId(id);
        if (patrocinio.getModalidade() != ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL) {
            return null; // sem efeito no preço
        }

        UUID eventoUuid = patrocinio.getEventoId().getValor();
        Evento evento = eventoRepositorio.obter(eventoUuid)
                .orElseThrow(() -> new IllegalStateException("Evento do patrocínio não encontrado: " + eventoUuid));

        BigDecimal precoAtual = evento.getPreco() != null && evento.getPreco().getInteira() != null
                ? evento.getPreco().getInteira()
                : BigDecimal.ZERO;

        ResultadoSubsidio subsidio = servico.calcularSubsidio(id, precoAtual);
        evento.aplicarSubsidioNoPreco(subsidio.getNovoPrecoSocial());
        eventoRepositorio.atualizar(evento);

        return subsidio;
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
