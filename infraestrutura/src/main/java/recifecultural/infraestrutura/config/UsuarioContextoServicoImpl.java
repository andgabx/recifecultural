package recifecultural.infraestrutura.config;

import org.springframework.stereotype.Service;
import recifecultural.dominio.compartilhado.notificacao.IUsuarioContextoServico;

import java.util.List;
import java.util.UUID;

/**
 * Implementação pragmática enquanto não há autenticação real.
 *
 * Resolve destinatários de broadcasts usando os IDs mock determinísticos
 * do frontend (espectador / produtor / gestor), de forma que a tela
 * `/notificacoes` mostre conteúdo de teste para o papel ativo no RoleSwitcher.
 *
 * Notificações dirigidas (`enviarNotificacao(usuarioAlvo, ...)`) não passam
 * por aqui — vão direto ao alvo via `INotificacaoRepositorio.salvar(...)`.
 */
@Service
class UsuarioContextoServicoImpl implements IUsuarioContextoServico {

    private static final UUID ESPECTADOR_DEMO =
            UUID.fromString("00000000-0000-0000-0000-000000000001");
    private static final UUID PRODUTOR_DEMO =
            UUID.fromString("00000000-0000-0000-0000-000000000002");
    private static final UUID GESTOR_DEMO =
            UUID.fromString("00000000-0000-0000-0000-000000000003");

    @Override
    public List<UUID> obterUsuariosPorContexto(String contexto, UUID idReferencia) {
        if (contexto == null) return List.of(ESPECTADOR_DEMO);
        return switch (contexto) {
            // Mudanças que afetam quem vai assistir ao evento.
            case "PARTICIPANTES_EVENTO_CANCELADO",
                 "ACESSIBILIDADE_REMOVIDA",
                 "EQUIPAMENTO_MANUTENCAO",
                 "SORTEIO_CANCELADO",
                 "TITULARES_INGRESSOS_EVENTO" -> List.of(ESPECTADOR_DEMO);
            // Artistas de um evento específico (idReferencia = eventoId).
            case "ARTISTAS_EVENTO" -> List.of(PRODUTOR_DEMO);
            // Todos os artistas cadastrados.
            case "TODOS_ARTISTAS" -> List.of(PRODUTOR_DEMO);
            // Comunicados administrativos: envolvem produtor e gestor.
            case "PROMOTORES",
                 "GESTORES_PROMOTORES" -> List.of(PRODUTOR_DEMO, GESTOR_DEMO);
            // Chamados de suporte técnico são tratados pelo gestor.
            case "SUPORTE_TECNICO" -> List.of(GESTOR_DEMO);
            case "TODOS_USUARIOS" -> List.of(ESPECTADOR_DEMO, PRODUTOR_DEMO, GESTOR_DEMO);
            default -> List.of(ESPECTADOR_DEMO);
        };
    }
}
