package recifecultural.infraestrutura.persistencia.espaco.setor;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.setor.Assento;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.SetorId;

import java.util.ArrayList;
import java.util.List;

public class SetorMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<SetorJpa, Setor>() {
            @Override
            protected Setor convert(SetorJpa s) {
                List<Assento> assentos = s.assentos == null ? List.of() :
                        s.assentos.stream()
                                .map(a -> new Assento(a.id, a.codigo, a.fileira, a.numero,
                                        a.status, a.motivoIndisponibilidade, a.versao))
                                .toList();
                return new Setor(
                        new SetorId(s.id), new EspacoId(s.espacoId),
                        s.nome, s.tipoSetor,
                        s.fileirasHorizontais, s.assentosPorFileiraVertical,
                        assentos, s.versao
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Setor, SetorJpa>() {
            @Override
            protected SetorJpa convert(Setor s) {
                var jpa = new SetorJpa();
                jpa.id = s.getId().valor();
                jpa.espacoId = s.getEspacoId().valor();
                jpa.nome = s.getNome();
                jpa.tipoSetor = s.getTipoSetor();
                jpa.fileirasHorizontais = s.getFileirasHorizontais();
                jpa.assentosPorFileiraVertical = s.getAssentosPorFileiraVertical();
                jpa.versao = s.getVersao();
                jpa.assentos = s.getAssentos().stream().map(a -> {
                    var aj = new AssentoJpa();
                    aj.id = a.getId();
                    aj.codigo = a.getCodigo();
                    aj.fileira = a.getFileira();
                    aj.numero = a.getNumero();
                    aj.status = a.getStatus();
                    aj.motivoIndisponibilidade = a.getMotivoIndisponibilidade();
                    aj.versao = a.getVersao();
                    return aj;
                }).collect(java.util.stream.Collectors.toCollection(ArrayList::new));
                return jpa;
            }
        });
    }
}
