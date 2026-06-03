package recifecultural.infraestrutura.persistencia.agenda.prereserva;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.agenda.prereserva.PreReserva;
import recifecultural.dominio.agenda.prereserva.PreReservaId;

public class PreReservaMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<PreReservaJpa, PreReserva>() {
            @Override
            protected PreReserva convert(PreReservaJpa s) {
                return new PreReserva(
                        new PreReservaId(s.id), s.assentoId, s.setorId,
                        s.usuarioId, s.criadaEm, s.expiraEm, s.status, s.versao
                );
            }
        });

        mapper.addConverter(new AbstractConverter<PreReserva, PreReservaJpa>() {
            @Override
            protected PreReservaJpa convert(PreReserva s) {
                var jpa = new PreReservaJpa();
                jpa.id = s.getId().valor();
                jpa.assentoId = s.getAssentoId();
                jpa.setorId = s.getSetorId();
                jpa.usuarioId = s.getUsuarioId();
                jpa.criadaEm = s.getCriadaEm();
                jpa.expiraEm = s.getExpiraEm();
                jpa.status = s.getStatus();
                jpa.versao = s.getVersao();
                return jpa;
            }
        });
    }
}
