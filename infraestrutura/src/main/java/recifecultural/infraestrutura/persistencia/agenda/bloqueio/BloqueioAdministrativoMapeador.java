package recifecultural.infraestrutura.persistencia.agenda.bloqueio;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativo;
import recifecultural.dominio.agenda.bloqueioadministrativo.BloqueioAdministrativoId;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BloqueioAdministrativoMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<BloqueioAdministrativoJpa, BloqueioAdministrativo>() {
            @Override
            protected BloqueioAdministrativo convert(BloqueioAdministrativoJpa s) {
                List<UUID> eventos = new ArrayList<>(
                        BloqueioAdministrativoJpa.parseEventosCancelados(s.eventosCancelados));
                return new BloqueioAdministrativo(
                        new BloqueioAdministrativoId(s.id),
                        new EspacoId(s.espacoId),
                        s.dataInicio, s.dataFim,
                        s.justificativa,
                        s.ativo,
                        eventos
                );
            }
        });

        mapper.addConverter(new AbstractConverter<BloqueioAdministrativo, BloqueioAdministrativoJpa>() {
            @Override
            protected BloqueioAdministrativoJpa convert(BloqueioAdministrativo s) {
                var jpa = new BloqueioAdministrativoJpa();
                jpa.id = s.getId().valor();
                jpa.espacoId = s.getEspacoId().valor();
                jpa.dataInicio = s.getDataInicio();
                jpa.dataFim = s.getDataFim();
                jpa.justificativa = s.getJustificativa();
                jpa.ativo = s.isAtivo();
                List<UUID> cancelados = s.getEventosCancelados();
                jpa.eventosCancelados = cancelados.isEmpty() ? null
                        : cancelados.stream().map(UUID::toString)
                                .collect(Collectors.joining(","));
                return jpa;
            }
        });
    }
}
