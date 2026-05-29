package recifecultural.apresentacao.bff.patrocinio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

record CriarPatrocinioRequisicao(
        UUID eventoId,
        String patrocinadorNome,
        String categoriaPatrocinio,
        String tipo,
        String modalidade,
        BigDecimal valorContribuicao,
        LocalDateTime dataEvento,
        boolean eventoAprovado) {}

record SimulacaoCancelamentoPatrocinio(BigDecimal valorReembolsado, BigDecimal multaAplicada) {}

record SimulacaoSubsidio(BigDecimal novoPrecoSocial, boolean pisoAplicado) {}
