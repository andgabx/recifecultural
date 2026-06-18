package recifecultural.aplicacao.patrocinio;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.IEventoRepositorio;
import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.IEventoParaSubsidio;
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

    public ResultadoSubsidio ativar(PatrocinioId id) {
        Patrocinio patrocinio = servico.obterPorId(id);
        UUID eventoUuid = patrocinio.getEventoId().getValor();
        Evento evento = eventoRepositorio.obter(eventoUuid)
                .orElseThrow(() -> new IllegalStateException("Evento do patrocínio não encontrado: " + eventoUuid));

        BigDecimal precoInteira = evento.getPreco() != null ? evento.getPreco().getInteira() : null;
        if (precoInteira != null) {
            BigDecimal maximo = precoInteira.subtract(new BigDecimal("1.00"));
            if (patrocinio.getValorContribuicao().compareTo(maximo) > 0) {
                throw new IllegalStateException("A contribuição do patrocínio não pode ser maior que o preço do ingresso menos o piso mínimo de R$1,00.");
            }
        }

        IEventoParaSubsidio eventoAdaptado = new IEventoParaSubsidio() {
            @Override
            public BigDecimal getPrecoInteiro() {
                return precoInteira;
            }

            @Override
            public void aplicarSubsidioNoPreco(BigDecimal novoPrecoSocial) {
                evento.aplicarSubsidioNoPreco(novoPrecoSocial);
            }
        };

        ResultadoSubsidio subsidio = servico.ativarComSubsidio(id, eventoAdaptado);
        if (subsidio != null) {
            eventoRepositorio.atualizar(evento);
        }

        return subsidio;
    }

    public ResultadoCancelamento cancelarPorEvento(PatrocinioId id, LocalDateTime agora) {
        ResultadoCancelamento resultado = servico.cancelarPorEvento(id, agora);
        removerSubsidioSeAplicavel(id);
        return resultado;
    }

    public ResultadoCancelamento cancelarPorPatrocinador(PatrocinioId id, LocalDateTime agora) {
        ResultadoCancelamento resultado = servico.cancelarPorPatrocinador(id, agora);
        removerSubsidioSeAplicavel(id);
        return resultado;
    }

    private void removerSubsidioSeAplicavel(PatrocinioId id) {
        var patrocinio = servico.obterPorId(id);
        if (patrocinio.getModalidade() != ModalidadeContribuicao.SUBSIDIO_INGRESSO_SOCIAL) return;
        UUID eventoUuid = patrocinio.getEventoId().getValor();
        eventoRepositorio.obter(eventoUuid).ifPresent(evento -> {
            evento.removerSubsidioDoPreco();
            eventoRepositorio.atualizar(evento);
        });
    }

    public ResultadoSubsidio calcularSubsidio(PatrocinioId id, BigDecimal precoSocialAtual) {
        return servico.calcularSubsidio(id, precoSocialAtual);
    }
}
