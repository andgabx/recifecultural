package recifecultural.infraestrutura.persistencia.compartilhado.notificacao;

import org.springframework.stereotype.Repository;

import recifecultural.dominio.compartilhado.notificacao.INotificacaoRepositorio;
import recifecultural.dominio.compartilhado.notificacao.Notificacao;
import recifecultural.dominio.compartilhado.notificacao.NotificacaoId;
import recifecultural.infraestrutura.persistencia.jpa.JpaMapeador;

import java.util.List;
import java.util.UUID;

@Repository
public class NotificacaoRepositorioImpl implements INotificacaoRepositorio {

    private final NotificacaoJpaRepository jpa;
    private final JpaMapeador mapeador;

    public NotificacaoRepositorioImpl(NotificacaoJpaRepository jpa, JpaMapeador mapeador) {
        this.jpa = jpa;
        this.mapeador = mapeador;
    }

    @Override
    public boolean salvar(Notificacao notificacao) {
        jpa.save(mapeador.map(notificacao, NotificacaoJpa.class));
        return true;
    }

    @Override
    public boolean atualizar(Notificacao notificacao) {
        jpa.save(mapeador.map(notificacao, NotificacaoJpa.class));
        return true;
    }

    @Override
    public Notificacao obter(NotificacaoId id) {
        return jpa.findById(id.valor())
                .map(n -> mapeador.map(n, Notificacao.class))
                .orElse(null);
    }

    @Override
    public List<Notificacao> obterPorUsuario(UUID usuarioAlvo) {
        return jpa.findByUsuarioAlvoOrderByDataCriacaoDesc(usuarioAlvo).stream()
                .map(n -> mapeador.map(n, Notificacao.class))
                .toList();
    }

    @Override
    public List<Notificacao> obterNaoLidasPorUsuario(UUID usuarioAlvo) {
        return jpa.findByUsuarioAlvoAndFoiLidaFalseOrderByDataCriacaoDesc(usuarioAlvo).stream()
                .map(n -> mapeador.map(n, Notificacao.class))
                .toList();
    }

    @Override
    public List<Notificacao> obterPorContexto(UUID usuarioAlvo, String contexto) {
        return jpa.findByUsuarioAlvoAndContextoOrderByDataCriacaoDesc(usuarioAlvo, contexto).stream()
                .map(n -> mapeador.map(n, Notificacao.class))
                .toList();
    }
}
