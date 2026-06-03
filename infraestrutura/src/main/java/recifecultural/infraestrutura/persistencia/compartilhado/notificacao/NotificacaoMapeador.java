package recifecultural.infraestrutura.persistencia.compartilhado.notificacao;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoId;

public class NotificacaoMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<NotificacaoJpa, Notificacao>() {
            @Override
            protected Notificacao convert(NotificacaoJpa s) {
                return new Notificacao(new NotificacaoId(s.id), s.usuarioAlvo,
                        s.mensagem, s.contexto, s.idReferencia, s.foiLida, s.dataCriacao);
            }
        });

        mapper.addConverter(new AbstractConverter<Notificacao, NotificacaoJpa>() {
            @Override
            protected NotificacaoJpa convert(Notificacao s) {
                var jpa = new NotificacaoJpa();
                jpa.id = s.getId().valor();
                jpa.usuarioAlvo = s.getUsuarioAlvo();
                jpa.mensagem = s.getMensagem();
                jpa.contexto = s.getContexto();
                jpa.idReferencia = s.getIdReferencia();
                jpa.foiLida = s.isFoiLida();
                jpa.dataCriacao = s.getDataCriacao();
                return jpa;
            }
        });
    }
}
