package recifecultural.infraestrutura.persistencia.espaco.suporte;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.espaco.suporte.ChamadoSuporte;

public class ChamadoSuporteMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<ChamadoSuporteJpa, ChamadoSuporte>() {
            @Override
            protected ChamadoSuporte convert(ChamadoSuporteJpa s) {
                return new ChamadoSuporte(s.id, s.assentoId, s.motivo, s.descricao, s.status, s.dataAbertura);
            }
        });

        mapper.addConverter(new AbstractConverter<ChamadoSuporte, ChamadoSuporteJpa>() {
            @Override
            protected ChamadoSuporteJpa convert(ChamadoSuporte s) {
                var jpa = new ChamadoSuporteJpa();
                jpa.id = s.getId();
                jpa.assentoId = s.getAssentoId();
                jpa.descricao = s.getDescricao();
                jpa.motivo = s.getMotivo();
                jpa.status = s.getStatus();
                jpa.dataAbertura = s.getDataAbertura();
                return jpa;
            }
        });
    }
}
