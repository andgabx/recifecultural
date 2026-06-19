package recifecultural.aplicacao.catraca;

import recifecultural.dominio.catraca.CatracaServico;
import recifecultural.dominio.compartilhado.evento.EventoBarramento;
import recifecultural.dominio.ingressos.Ingresso;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.Validate.notNull;

@Transactional(readOnly = true)
public class CatracaServicoAplicacao {

    private final CatracaServico servico;

    public CatracaServicoAplicacao(CatracaServico servico, EventoBarramento barramento) {
        notNull(servico, "CatracaServico não pode ser nulo.");
        notNull(barramento, "EventoBarramento não pode ser nulo.");
        this.servico = servico;
        barramento.adicionar(this::tratarReembolso);
    }

    @Transactional
    public String validarAcesso(String idIngresso, LocalDateTime horario, String portao) {
        return servico.validarAcesso(idIngresso, horario, portao);
    }

    @Transactional
    private void tratarReembolso(Ingresso.ReembolsadoEvento evento) {
        servico.inativarIngresso(evento.getIngresso().getCodigoQr());
    }
}
