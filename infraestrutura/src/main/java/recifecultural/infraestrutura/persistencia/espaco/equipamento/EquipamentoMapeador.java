package recifecultural.infraestrutura.persistencia.espaco.equipamento;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.espaco.espaco.EspacoId;

public class EquipamentoMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<EquipamentoJpa, Equipamento>() {
            @Override
            protected Equipamento convert(EquipamentoJpa s) {
                return new Equipamento(new EquipamentoId(s.id), new EspacoId(s.espacoId),
                        s.nome, s.status, s.eventoAlocadoId, s.alocacaoInicio, s.alocacaoFim);
            }
        });

        mapper.addConverter(new AbstractConverter<Equipamento, EquipamentoJpa>() {
            @Override
            protected EquipamentoJpa convert(Equipamento s) {
                var jpa = new EquipamentoJpa();
                jpa.id = s.getId().valor();
                jpa.espacoId = s.getEspacoId().valor();
                jpa.nome = s.getNome();
                jpa.status = s.getStatus();
                jpa.eventoAlocadoId = s.getEventoAlocadoId();
                jpa.alocacaoInicio = s.getAlocacaoInicio();
                jpa.alocacaoFim = s.getAlocacaoFim();
                return jpa;
            }
        });
    }
}
