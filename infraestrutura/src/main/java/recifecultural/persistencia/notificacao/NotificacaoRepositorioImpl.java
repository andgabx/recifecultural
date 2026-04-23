package recifecultural.persistencia.notificacao;

import org.springframework.stereotype.Component;
import recifecultural.dominio.agenda.notificacao.INotificacaoRepositorio;
import recifecultural.dominio.agenda.notificacao.Notificacao;
import recifecultural.dominio.agenda.notificacao.NotificacaoId;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class NotificacaoRepositorioImpl implements INotificacaoRepositorio {

    private final NotificacaoJpaRepositorio repositorio;

    public NotificacaoRepositorioImpl(NotificacaoJpaRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public boolean salvar(Notificacao notificacao) {
        repositorio.save(toJpa(notificacao));
        return true;
    }

    @Override
    public boolean atualizar(Notificacao notificacao) {
        repositorio.save(toJpa(notificacao));
        return true;
    }

    @Override
    public Notificacao obter(NotificacaoId id) {
        return repositorio.findById(id.valor()).map(this::toDomain).orElse(null);
    }

    @Override
    public List<Notificacao> obterPorUsuario(UUID usuarioAlvo) {
        return repositorio.findRelevantesParaUsuario(usuarioAlvo)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Notificacao> obterNaoLidasPorUsuario(UUID usuarioAlvo) {
        return obterPorUsuario(usuarioAlvo).stream()
                .filter(n -> !n.isLidaPor(usuarioAlvo))
                .collect(Collectors.toList());
    }

    private NotificacaoJpa toJpa(Notificacao dominio) {
        return new NotificacaoJpa(
                dominio.getId().valor(),
                dominio.getUsuarioAlvo(),
                dominio.getMensagem(),
                dominio.isFoiLida(),
                dominio.isBroadcast(),
                dominio.getLidaPor(),
                dominio.getDataCriacao()
        );
    }

    private Notificacao toDomain(NotificacaoJpa jpa) {
        return new Notificacao(
                new NotificacaoId(jpa.getId()),
                jpa.getUsuarioAlvo(),
                jpa.getMensagem(),
                jpa.isFoiLida(),
                jpa.isBroadcast(),
                jpa.getLidaPor(),
                jpa.getDataCriacao()
        );
    }
}