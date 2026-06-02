package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.apache.commons.lang3.Validate.isTrue;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

public class PatrocinioServico {

    private final IPatrocinioRepositorio repositorio;

    public PatrocinioServico(IPatrocinioRepositorio repositorio) {
        notNull(repositorio, "O repositório de patrocínio não pode ser nulo.");
        this.repositorio = repositorio;
    }

    public Patrocinio criar(EventoId eventoId,
                            String patrocinadorNome,
                            String categoriaPatrocinio,
                            TipoPatrocinio tipo,
                            ModalidadeContribuicao modalidade,
                            BigDecimal valorContribuicao,
                            LocalDateTime dataEvento,
                            boolean eventoAprovado) {
        notNull(eventoId, "O id do evento não pode ser nulo.");
        notBlank(patrocinadorNome, "O nome do patrocinador não pode ser vazio.");
        notBlank(categoriaPatrocinio, "A categoria do patrocínio não pode ser vazia.");
        notNull(tipo, "O tipo do patrocínio não pode ser nulo.");
        notNull(modalidade, "A modalidade de contribuição não pode ser nula.");
        notNull(valorContribuicao, "O valor da contribuição não pode ser nulo.");
        notNull(dataEvento, "A data do evento não pode ser nula.");

        isTrue(eventoAprovado, "O patrocínio só pode ser criado para eventos com status APROVADO.");

        if (tipo == TipoPatrocinio.MASTER) {
            isTrue(repositorio.buscarMasterPorEvento(eventoId).isEmpty(),
                    "Já existe um patrocinador MASTER para este evento.");
        }

        isTrue(repositorio.buscarPorEventoECategoria(eventoId, categoriaPatrocinio).isEmpty(),
                "Já existe um patrocinador da categoria '" + categoriaPatrocinio + "' para este evento.");

        Patrocinio patrocinio = new Patrocinio(
                PatrocinioId.novo(),
                eventoId,
                patrocinadorNome,
                categoriaPatrocinio,
                tipo,
                modalidade,
                valorContribuicao,
                dataEvento
        );

        repositorio.salvar(patrocinio);
        return patrocinio;
    }

    public Patrocinio obterPorId(PatrocinioId id) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        Patrocinio patrocinio = repositorio.buscarPorId(id);
        notNull(patrocinio, "Patrocínio não encontrado com id: " + id);
        return patrocinio;
    }

    public void ativar(PatrocinioId id) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        Patrocinio patrocinio = repositorio.buscarPorId(id);
        notNull(patrocinio, "Patrocínio não encontrado com id: " + id);
        patrocinio.ativar();
        repositorio.atualizar(patrocinio);
    }

    public void encerrar(PatrocinioId id) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        Patrocinio patrocinio = repositorio.buscarPorId(id);
        notNull(patrocinio, "Patrocínio não encontrado com id: " + id);
        patrocinio.encerrar();
        repositorio.atualizar(patrocinio);
    }

    public ResultadoCancelamento cancelarPorEvento(PatrocinioId id, LocalDateTime agora) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        notNull(agora, "A data/hora atual não pode ser nula.");
        Patrocinio patrocinio = repositorio.buscarPorId(id);
        notNull(patrocinio, "Patrocínio não encontrado com id: " + id);
        Patrocinio.CanceladoEvento evento = patrocinio.cancelarPorEvento(agora);
        repositorio.atualizar(patrocinio);
        return new ResultadoCancelamento(evento.getReembolso(), evento.getMulta(), evento.getMotivo());
    }

    public ResultadoCancelamento cancelarPorPatrocinador(PatrocinioId id, LocalDateTime agora) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        notNull(agora, "A data/hora atual não pode ser nula.");
        Patrocinio patrocinio = repositorio.buscarPorId(id);
        notNull(patrocinio, "Patrocínio não encontrado com id: " + id);
        Patrocinio.CanceladoEvento evento = patrocinio.cancelarPorPatrocinador(agora);
        repositorio.atualizar(patrocinio);
        return new ResultadoCancelamento(evento.getReembolso(), evento.getMulta(), evento.getMotivo());
    }

    public ResultadoSubsidio calcularSubsidio(PatrocinioId id, BigDecimal precoSocialAtual) {
        notNull(id, "O id do patrocínio não pode ser nulo.");
        notNull(precoSocialAtual, "O preço social atual não pode ser nulo.");
        Patrocinio patrocinio = repositorio.buscarPorId(id);
        notNull(patrocinio, "Patrocínio não encontrado com id: " + id);
        return patrocinio.calcularSubsidio(precoSocialAtual);
    }
}
