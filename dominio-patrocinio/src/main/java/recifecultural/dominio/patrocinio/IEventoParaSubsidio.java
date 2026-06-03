package recifecultural.dominio.patrocinio;

import java.math.BigDecimal;

/**
 * Porta que descreve as operações de um Evento necessárias para aplicar
 * o subsídio de ingresso social. Mantém o domínio de patrocínio desacoplado
 * do domínio de agenda.
 */
public interface IEventoParaSubsidio {

    BigDecimal getPrecoInteiro();

    void aplicarSubsidioNoPreco(BigDecimal novoPrecoSocial);
}
