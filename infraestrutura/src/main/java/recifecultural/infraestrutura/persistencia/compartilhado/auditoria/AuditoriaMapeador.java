package recifecultural.infraestrutura.persistencia.compartilhado.auditoria;

import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;

import recifecultural.dominio.compartilhado.auditoria.RegistroAuditoria;

public class AuditoriaMapeador {

    public static void registrar(ModelMapper mapper) {
        mapper.addConverter(new AbstractConverter<AuditoriaJpa, RegistroAuditoria>() {
            @Override
            protected RegistroAuditoria convert(AuditoriaJpa s) {
                return new RegistroAuditoria(
                        s.id, s.entidade, s.entidadeId, s.acao,
                        s.statusAnterior, s.statusNovo, s.descricao, s.momento);
            }
        });

        mapper.addConverter(new AbstractConverter<RegistroAuditoria, AuditoriaJpa>() {
            @Override
            protected AuditoriaJpa convert(RegistroAuditoria s) {
                var jpa = new AuditoriaJpa();
                jpa.id = s.getId();
                jpa.entidade = s.getEntidade();
                jpa.entidadeId = s.getEntidadeId();
                jpa.acao = s.getAcao();
                jpa.statusAnterior = s.getStatusAnterior();
                jpa.statusNovo = s.getStatusNovo();
                jpa.descricao = s.getDescricao();
                jpa.momento = s.getMomento();
                return jpa;
            }
        });
    }
}
