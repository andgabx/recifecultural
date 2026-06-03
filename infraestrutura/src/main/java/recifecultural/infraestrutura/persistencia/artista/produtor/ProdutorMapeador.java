package recifecultural.infraestrutura.persistencia.artista.produtor;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.HistoricoStatusProdutor;
import recifecultural.dominio.artista.produtor.Produtor;
import recifecultural.dominio.artista.produtor.ProdutorId;

@Component
public class ProdutorMapeador {

    private final ModelMapper modelMapper;

    public ProdutorMapeador() {
        this.modelMapper = new ModelMapper();
        registrar(this.modelMapper);
    }

    public Produtor toDomain(ProdutorJpa s) {
        return modelMapper.map(s, Produtor.class);
    }

    public ProdutorJpa toJpa(Produtor s) {
        return modelMapper.map(s, ProdutorJpa.class);
    }

    public HistoricoStatusProdutor toDomain(HistoricoStatusProdutorJpa s) {
        return modelMapper.map(s, HistoricoStatusProdutor.class);
    }

    public HistoricoStatusProdutorJpa toJpa(HistoricoStatusProdutor s) {
        return modelMapper.map(s, HistoricoStatusProdutorJpa.class);
    }

    public static void registrar(ModelMapper mapper) {
        registrarProdutor(mapper);
        registrarHistoricoStatus(mapper);
    }

    private static void registrarProdutor(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<ProdutorJpa, Produtor>() {
            @Override
            protected Produtor convert(ProdutorJpa s) {
                return new Produtor(
                        new ProdutorId(s.id),
                        s.nomeFantasia,
                        s.cnpj != null ? new Cnpj(s.cnpj) : null,
                        s.email, s.telefone, s.status
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Produtor, ProdutorJpa>() {
            @Override
            protected ProdutorJpa convert(Produtor s) {
                var jpa = new ProdutorJpa();
                jpa.id = s.getId().valor();
                jpa.nomeFantasia = s.getNomeFantasia();
                jpa.cnpj = s.getCnpj() != null ? s.getCnpj().valor() : null;
                jpa.email = s.getEmail();
                jpa.telefone = s.getTelefone();
                jpa.status = s.getStatus();
                return jpa;
            }
        });
    }

    private static void registrarHistoricoStatus(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<HistoricoStatusProdutorJpa, HistoricoStatusProdutor>() {
            @Override
            protected HistoricoStatusProdutor convert(HistoricoStatusProdutorJpa s) {
                return new HistoricoStatusProdutor(
                        new ProdutorId(s.produtorId),
                        s.statusAnterior, s.statusNovo, s.responsavel, s.motivo);
            }
        });

        mapper.addConverter(new AbstractConverter<HistoricoStatusProdutor, HistoricoStatusProdutorJpa>() {
            @Override
            protected HistoricoStatusProdutorJpa convert(HistoricoStatusProdutor s) {
                var jpa = new HistoricoStatusProdutorJpa();
                jpa.id = s.getId();
                jpa.produtorId = s.getProdutorId().valor();
                jpa.statusAnterior = s.getStatusAnterior();
                jpa.statusNovo = s.getStatusNovo();
                jpa.responsavel = s.getResponsavel();
                jpa.motivo = s.getMotivo();
                jpa.dataAlteracao = s.getDataAlteracao();
                return jpa;
            }
        });
    }
}
