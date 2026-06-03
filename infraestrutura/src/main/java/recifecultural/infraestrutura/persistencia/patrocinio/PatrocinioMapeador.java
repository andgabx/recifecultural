package recifecultural.infraestrutura.persistencia.patrocinio;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.patrocinio.EventoId;
import recifecultural.dominio.patrocinio.ModalidadeContribuicao;
import recifecultural.dominio.patrocinio.Patrocinio;
import recifecultural.dominio.patrocinio.PatrocinioId;

public class PatrocinioMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<PatrocinioJpa, Patrocinio>() {
            @Override
            protected Patrocinio convert(PatrocinioJpa s) {
                return new Patrocinio(
                        new PatrocinioId(s.id),
                        new EventoId(s.eventoId),
                        s.patrocinadorNome, s.categoriaPatrocinio,
                        recifecultural.dominio.patrocinio.TipoPatrocinio.valueOf(s.tipo),
                        ModalidadeContribuicao.valueOf(s.modalidade),
                        s.valorContribuicao, s.dataEvento,
                        s.status, s.valorReembolsado, s.multaAplicada
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Patrocinio, PatrocinioJpa>() {
            @Override
            protected PatrocinioJpa convert(Patrocinio s) {
                var jpa = new PatrocinioJpa();
                jpa.id = s.getId().getValor();
                jpa.eventoId = s.getEventoId().getValor();
                jpa.patrocinadorNome = s.getPatrocinadorNome();
                jpa.categoriaPatrocinio = s.getCategoriaPatrocinio();
                jpa.tipo = s.getTipo().name();
                jpa.modalidade = s.getModalidade().name();
                jpa.valorContribuicao = s.getValorContribuicao();
                jpa.dataEvento = s.getDataEvento();
                jpa.status = s.getStatus();
                jpa.valorReembolsado = s.getValorReembolsado();
                jpa.multaAplicada = s.getMultaAplicada();
                return jpa;
            }
        });
    }
}
