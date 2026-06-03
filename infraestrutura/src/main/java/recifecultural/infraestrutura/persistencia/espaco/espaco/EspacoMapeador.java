package recifecultural.infraestrutura.persistencia.espaco.espaco;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.ArrayList;
import java.util.List;

public class EspacoMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<EspacoJpa, Espaco>() {
            @Override
            protected Espaco convert(EspacoJpa s) {
                List<String> rider = s.riderTecnico != null ? new ArrayList<>(s.riderTecnico) : List.of();
                return new Espaco(new EspacoId(s.id), s.nome, s.capacidadeMaxima, rider, s.status);
            }
        });

        mapper.addConverter(new AbstractConverter<Espaco, EspacoJpa>() {
            @Override
            protected EspacoJpa convert(Espaco s) {
                var jpa = new EspacoJpa();
                jpa.id = s.getId().valor();
                jpa.nome = s.getNome();
                jpa.capacidadeMaxima = s.getCapacidadeMaxima();
                jpa.status = s.getStatus();
                jpa.riderTecnico = new ArrayList<>(s.getRiderTecnico());
                return jpa;
            }
        });
    }
}
