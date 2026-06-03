package recifecultural.infraestrutura.persistencia.catraca;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.catraca.IngressoCatraca;
import recifecultural.dominio.catraca.IngressoCatracaId;

public class CatracaMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<IngressoCatracaJpa, IngressoCatraca>() {
            @Override
            protected IngressoCatraca convert(IngressoCatracaJpa s) {
                return new IngressoCatraca(
                        new IngressoCatracaId(s.id),
                        s.idEvento, s.status,
                        s.horarioInicioEvento, s.tipoIngresso,
                        s.portaoAcesso
                );
            }
        });

        mapper.addConverter(new AbstractConverter<IngressoCatraca, IngressoCatracaJpa>() {
            @Override
            protected IngressoCatracaJpa convert(IngressoCatraca s) {
                var jpa = new IngressoCatracaJpa();
                jpa.id = s.getId().getValor();
                jpa.idEvento = s.getIdEvento();
                jpa.status = s.getStatus();
                jpa.horarioInicioEvento = s.getHorarioInicioEvento();
                jpa.tipoIngresso = s.getTipoIngresso();
                jpa.portaoAcesso = s.getPortaoAcesso();
                return jpa;
            }
        });
    }
}
