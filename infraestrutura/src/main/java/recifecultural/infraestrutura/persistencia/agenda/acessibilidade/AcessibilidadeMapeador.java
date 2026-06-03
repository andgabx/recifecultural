package recifecultural.infraestrutura.persistencia.agenda.acessibilidade;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.agenda.acessibilidade.RecursoAcessibilidade;

public class AcessibilidadeMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<RecursoAcessibilidadeJpa, RecursoAcessibilidade>() {
            @Override
            protected RecursoAcessibilidade convert(RecursoAcessibilidadeJpa s) {
                return new RecursoAcessibilidade(s.id, s.apresentacaoId, s.eventoId,
                        s.tipo, s.status, s.justificativaRemocao);
            }
        });

        mapper.addConverter(new AbstractConverter<RecursoAcessibilidade, RecursoAcessibilidadeJpa>() {
            @Override
            protected RecursoAcessibilidadeJpa convert(RecursoAcessibilidade s) {
                var jpa = new RecursoAcessibilidadeJpa();
                jpa.id = s.getId();
                jpa.apresentacaoId = s.getApresentacaoId();
                jpa.eventoId = s.getEventoId();
                jpa.tipo = s.getTipo();
                jpa.status = s.getStatus();
                jpa.justificativaRemocao = s.getJustificativaRemocao();
                return jpa;
            }
        });
    }
}
