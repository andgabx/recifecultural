package recifecultural.infraestrutura.persistencia.artista.artista;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import recifecultural.dominio.artista.artista.Artista;
import recifecultural.dominio.artista.artista.ArtistaId;
import recifecultural.dominio.artista.artista.RiderTecnico;
import recifecultural.dominio.artista.produtor.ProdutorId;

import java.util.HashSet;

@Component
public class ArtistaMapeador {

    private final ModelMapper modelMapper;

    public ArtistaMapeador() {
        this.modelMapper = new ModelMapper();
        registrar(this.modelMapper);
    }

    public Artista toDomain(ArtistaJpa s) {
        return modelMapper.map(s, Artista.class);
    }

    public ArtistaJpa toJpa(Artista s) {
        return modelMapper.map(s, ArtistaJpa.class);
    }

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<ArtistaJpa, Artista>() {
            @Override
            protected Artista convert(ArtistaJpa s) {
                RiderTecnico rider = (s.riderItens != null && !s.riderItens.isEmpty())
                        ? new RiderTecnico(s.riderItens)
                        : null;
                return new Artista(
                        new ArtistaId(s.id),
                        new ProdutorId(s.produtorId),
                        s.nome, rider, s.status
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Artista, ArtistaJpa>() {
            @Override
            protected ArtistaJpa convert(Artista s) {
                var jpa = new ArtistaJpa();
                jpa.id = s.getId().valor();
                jpa.produtorId = s.getProdutorId().valor();
                jpa.nome = s.getNome();
                jpa.status = s.getStatus();
                jpa.riderItens = s.getRiderTecnico() != null
                        ? new HashSet<>(s.getRiderTecnico().itens())
                        : new HashSet<>();
                return jpa;
            }
        });
    }
}
