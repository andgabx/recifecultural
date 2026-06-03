package recifecultural.infraestrutura.persistencia.agenda.sorteio;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import recifecultural.dominio.agenda.sorteio.Inscricao;
import recifecultural.dominio.agenda.sorteio.Sorteio;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SorteioMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<SorteioJpa, Sorteio>() {
            @Override
            protected Sorteio convert(SorteioJpa s) {
                List<Inscricao> inscricoes = s.inscricoes == null ? List.of() :
                        s.inscricoes.stream()
                                .map(i -> new Inscricao(i.espectadorId, i.momentoInscricao, i.status))
                                .toList();
                return new Sorteio(
                        s.id, s.apresentacaoId, s.eventoId, s.vagas,
                        s.prazoInscricao, s.dataApresentacao,
                        s.status, inscricoes
                );
            }
        });

        mapper.addConverter(new AbstractConverter<Sorteio, SorteioJpa>() {
            @Override
            protected SorteioJpa convert(Sorteio s) {
                var jpa = new SorteioJpa();
                jpa.id = s.getId();
                jpa.apresentacaoId = s.getApresentacaoId();
                jpa.eventoId = s.getEventoId();
                jpa.vagas = s.getVagas();
                jpa.prazoInscricao = s.getPrazoInscricao();
                jpa.dataApresentacao = s.getDataApresentacao();
                jpa.status = s.getStatus();
                jpa.inscricoes = s.getInscricoes().stream().map(i -> {
                    var ij = new InscricaoJpa();
                    ij.espectadorId = i.getEspectadorId();
                    ij.momentoInscricao = i.getMomentoInscricao();
                    ij.status = i.getStatus();
                    return ij;
                }).collect(Collectors.toCollection(ArrayList::new));
                return jpa;
            }
        });
    }

    public SorteioJpa toJpa(Sorteio s) {
        var jpa = new SorteioJpa();
        jpa.id = s.getId();
        jpa.apresentacaoId = s.getApresentacaoId();
        jpa.eventoId = s.getEventoId();
        jpa.vagas = s.getVagas();
        jpa.prazoInscricao = s.getPrazoInscricao();
        jpa.dataApresentacao = s.getDataApresentacao();
        jpa.status = s.getStatus();
        jpa.inscricoes = s.getInscricoes().stream().map(i -> {
            var ij = new InscricaoJpa();
            ij.espectadorId = i.getEspectadorId();
            ij.momentoInscricao = i.getMomentoInscricao();
            ij.status = i.getStatus();
            return ij;
        }).collect(Collectors.toCollection(ArrayList::new));
        return jpa;
    }

    public Sorteio toDomain(SorteioJpa s) {
        List<Inscricao> inscricoes = s.inscricoes == null ? List.of() :
                s.inscricoes.stream()
                        .map(i -> new Inscricao(i.espectadorId, i.momentoInscricao, i.status))
                        .toList();
        return new Sorteio(
                s.id, s.apresentacaoId, s.eventoId, s.vagas,
                s.prazoInscricao, s.dataApresentacao,
                s.status, inscricoes
        );
    }
}
