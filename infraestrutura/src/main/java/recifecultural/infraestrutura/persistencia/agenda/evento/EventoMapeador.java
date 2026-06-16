package recifecultural.infraestrutura.persistencia.agenda.evento;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.agenda.evento.Evento;
import recifecultural.dominio.agenda.evento.Periodo;
import recifecultural.dominio.agenda.evento.Preco;

import java.util.ArrayList;
import java.util.List;

public class EventoMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<EventoJpa, Evento>() {
            @Override
            protected Evento convert(EventoJpa s) {
                Evento evento = new Evento(
                        s.id, s.promotorId, s.localId,
                        s.titulo, s.descricaoCurta, s.descricaoLonga,
                        new Periodo(s.periodoInicio, s.periodoFim),
                        (s.precoInteira != null || s.precoSocial != null)
                                ? new Preco(s.precoInteira, s.precoMeia, s.precoSocial) : null,
                        s.categoria,
                        s.status,
                        s.datasApresentacao,
                        s.artistas,
                        s.dataAprovacao,
                        s.dataReprovacao,
                        s.requerRevisaoAdicional,
                        s.motivoCancelamento
                );
                if (s.riderItems != null) {
                    for (EventoJpa.RiderItemJpa item : s.riderItems) {
                        if (item != null && item.getNomeEquipamento() != null) {
                            evento.adicionarRiderItem(item.getNomeEquipamento(), item.getQuantidade());
                        }
                    }
                }
                return evento;
            }
        });

        mapper.addConverter(new AbstractConverter<Evento, EventoJpa>() {
            @Override
            protected EventoJpa convert(Evento s) {
                var jpa = new EventoJpa();
                jpa.id = s.getId();
                jpa.promotorId = s.getPromotorId();
                jpa.localId = s.getLocalId();
                jpa.titulo = s.getTitulo();
                jpa.descricaoCurta = s.getDescricaoCurta();
                jpa.descricaoLonga = s.getDescricaoLonga();
                if (s.getPeriodo() != null) {
                    jpa.periodoInicio = s.getPeriodo().getInicio();
                    jpa.periodoFim = s.getPeriodo().getFim();
                }
                jpa.categoria = s.getCategoria();
                jpa.status = s.getStatus();
                if (s.getPreco() != null) {
                    jpa.precoInteira = s.getPreco().getInteira();
                    jpa.precoMeia = s.getPreco().getMeia();
                    jpa.precoSocial = s.getPreco().getSocial();
                }
                jpa.dataAprovacao = s.getDataAprovacao();
                jpa.dataReprovacao = s.getDataReprovacao();
                jpa.requerRevisaoAdicional = s.isRequerRevisaoAdicional();
                jpa.motivoCancelamento = s.getMotivoCancelamento();
                jpa.datasApresentacao = new ArrayList<>(s.getDatasApresentacao());
                jpa.artistas = new ArrayList<>(s.getArtistas());
                List<EventoJpa.RiderItemJpa> riderItemsJpa = new ArrayList<>();
                for (var item : s.getRiderItems()) {
                    riderItemsJpa.add(new EventoJpa.RiderItemJpa(item.getNomeEquipamento(), item.getQuantidade()));
                }
                jpa.setRiderItems(riderItemsJpa);
                return jpa;
            }
        });
    }
}
