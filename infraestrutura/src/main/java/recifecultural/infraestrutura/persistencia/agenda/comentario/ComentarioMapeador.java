package recifecultural.infraestrutura.persistencia.agenda.comentario;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import recifecultural.dominio.agenda.comentario.Comentario;
import recifecultural.dominio.agenda.comentario.Nota;

import java.util.HashSet;

@Component
public class ComentarioMapeador {

    private final ModelMapper modelMapper;

    public ComentarioMapeador() {
        this.modelMapper = new ModelMapper();
        registrar(this.modelMapper);
    }

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<ComentarioJpa, Comentario>() {
            @Override
            protected Comentario convert(ComentarioJpa s) {
                Nota nota = s.nota != null ? new Nota(s.nota) : null;
                return new Comentario(
                        s.id, s.espectadorId, s.eventoId, s.comentarioPaiId,
                        s.texto, nota, s.status, s.criadoEm, s.curtidas);
            }
        });

        mapper.addConverter(new AbstractConverter<Comentario, ComentarioJpa>() {
            @Override
            protected ComentarioJpa convert(Comentario s) {
                var jpa = new ComentarioJpa();
                jpa.id = s.getId();
                jpa.espectadorId = s.getEspectadorId();
                jpa.eventoId = s.getEventoId();
                jpa.comentarioPaiId = s.getComentarioPaiId();
                jpa.texto = s.getTexto();
                jpa.nota = s.getNota() != null ? s.getNota().getValor() : null;
                jpa.status = s.getStatus();
                jpa.criadoEm = s.getCriadoEm();
                jpa.curtidas = new HashSet<>(s.getCurtidas());
                return jpa;
            }
        });
    }

    public Comentario toDominio(ComentarioJpa jpa) {
        return modelMapper.map(jpa, Comentario.class);
    }

    public ComentarioJpa toJpa(Comentario comentario) {
        return modelMapper.map(comentario, ComentarioJpa.class);
    }
}
