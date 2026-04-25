package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.*;
import recifecultural.dominio.agenda.espaco.EspacoId;
import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.StatusEquipamento;
import recifecultural.dominio.agenda.evento.Evento;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class PassosAlocacaoRiderTecnico {

    private String nomeEqInput;

    private final ContextoCenario contexto;
    private Equipamento equipamentoMock;
    private EspacoId espacoIdMock = EspacoId.novo();
    private UUID eventoIdMock = UUID.randomUUID();

    public PassosAlocacaoRiderTecnico(ContextoCenario contexto) {
        this.contexto = contexto;
    }

    @Dado("que o teatro possui {int} unidades de {string} disponíveis")
    public void queOTeatroPossuiUnidadesDeDisponiveis(int qtd, String nomeEq) {
        equipamentoMock = new Equipamento(espacoIdMock, nomeEq);
        contexto.idEquipamentoAtual = equipamentoMock.getId();

        when(contexto.repositorioEquipamento.buscarDisponiveisPorEspacoENome(espacoIdMock, nomeEq, 1))
                .thenReturn(List.of(equipamentoMock));
    }

    @E("um evento foi aprovado necessitando de {int} {string}")
    public void umEventoFoiAprovadoNecessitandoDe(int qtd, String nomeEq) {
        // Mock preparado no passo anterior
    }

    @Quando("o sistema processar a alocacao do rider tecnico")
    public void oSistemaProcessarAAlocacaoDoRiderTecnico() {
        contexto.servicoAlocacao.alocarEquipamentos(eventoIdMock, espacoIdMock, equipamentoMock.getNome(), 1);
    }

    @Entao("o status do equipamento deve mudar para {string}")
    public void oStatusDoEquipamentoDeveMudarPara(String statusEsperado) {
        assertEquals(StatusEquipamento.valueOf(statusEsperado), equipamentoMock.getStatus());
        assertEquals(eventoIdMock, equipamentoMock.getEventoAlocadoId());
        verify(contexto.repositorioEquipamento, times(1)).atualizar(equipamentoMock);
    }

    @Dado("que uma {string} esta com status {string} para o {string}")
    public void queUmaEstaComStatusParaO(String nomeEq, String status, String nomeEvento) {
        equipamentoMock = new Equipamento(espacoIdMock, nomeEq);
        equipamentoMock.alocarParaEvento(eventoIdMock);
        contexto.idEquipamentoAtual = equipamentoMock.getId();

        contexto.evento = mock(Evento.class);
        when(contexto.evento.getPromotorId()).thenReturn(UUID.randomUUID());
        when(contexto.evento.getTitulo()).thenReturn(nomeEvento);

        when(contexto.repositorioEquipamento.obterPorId(contexto.idEquipamentoAtual)).thenReturn(Optional.of(equipamentoMock));
        when(contexto.repositorioEvento.obter(eventoIdMock)).thenReturn(Optional.of(contexto.evento));
        when(contexto.repositorioEquipamento.buscarAlocadosPorEvento(eventoIdMock)).thenReturn(List.of(equipamentoMock));
    }

    @Quando("o zelador registrar que a {string} foi para manutencao")
    public void oZeladorRegistrarQueAFoiParaManutencao(String nomeEq) {
        contexto.servicoAlocacao.registrarManutencao(contexto.idEquipamentoAtual);
    }

    // NOVA VALIDAÇÃO DE NOTIFICAÇÃO
    @E("o sistema deve enviar uma notificacao de alerta para o promotor do {string}")
    public void oSistemaDeveEnviarUmaNotificacaoDeAlertaParaOPromotorDo(String nomeEvento) {
        // Verifica se o método enviarNotificacao foi chamado com o ID do promotor do evento e qualquer string de mensagem
        verify(contexto.servicoNotificacao, times(1)).enviarNotificacao(eq(contexto.evento.getPromotorId()), anyString());
    }

    @Quando("o {string} for cancelado")
    public void oForCancelado(String nomeEvento) {
        contexto.servicoAlocacao.desmobilizarEquipamentosDoEvento(eventoIdMock);
    }

    @Entao("a {string} deve ser devolvida ao pool com status {string}")
    public void aDeveSerDevolvidaAoPoolComStatus(String nomeEq, String statusEsperado) {
        assertEquals(StatusEquipamento.valueOf(statusEsperado), equipamentoMock.getStatus());
        assertNull(equipamentoMock.getEventoAlocadoId());
        verify(contexto.repositorioEquipamento, times(1)).atualizar(equipamentoMock);
    }

    @Dado("que o teatro adquiriu um novo {string}")
    public void queOTeatroAdquiriuUmNovo(String nomeEq) {
        this.nomeEqInput = nomeEq;
    }

    @Quando("eu confirmar o registro no sistema")
    public void euConfirmarORegistroNoSistema() {
        contexto.idEquipamentoAtual = contexto.servicoEquipamento.adquirirEquipamento(espacoIdMock, nomeEqInput);

        Equipamento novo = new Equipamento(espacoIdMock, nomeEqInput);
        when(contexto.repositorioEquipamento.obterPorId(contexto.idEquipamentoAtual)).thenReturn(Optional.of(novo));
    }

    @Entao("o {string} deve aparecer no inventario como {string}")
    public void oDeveAparecerNoInventarioComo(String nome, String status) {
        Equipamento eq = contexto.repositorioEquipamento.obterPorId(contexto.idEquipamentoAtual).get();
        assertEquals(StatusEquipamento.valueOf(status), eq.getStatus());
        verify(contexto.repositorioEquipamento).salvar(any(Equipamento.class));
    }

    @Dado("que o equipamento {string} foi extraviado")
    public void queOEquipamentoFoiExtraviado(String nome) {
        equipamentoMock = new Equipamento(espacoIdMock, nome);
        contexto.idEquipamentoAtual = equipamentoMock.getId();
        when(contexto.repositorioEquipamento.obterPorId(contexto.idEquipamentoAtual)).thenReturn(Optional.of(equipamentoMock));
    }

    @Quando("eu solicitar a exclusao definitiva do equipamento")
    public void euSolicitarAExclusaoDefinitivaDoEquipamento() {
        contexto.servicoEquipamento.removerEquipamento(contexto.idEquipamentoAtual);
    }

    @Entao("o equipamento nao deve mais existir no repositorio")
    public void oEquipamentoNaoDeveMaisExistirNoRepositorio() {
        verify(contexto.repositorioEquipamento).deletar(contexto.idEquipamentoAtual);
    }

    @Quando("eu tentar excluir definitivamente a {string}")
    public void euTentarExcluirDefinitivamenteA(String nome) {
        try {
            contexto.servicoEquipamento.removerEquipamento(contexto.idEquipamentoAtual);
        } catch (Exception e) {
            contexto.excecaoCapturada = e;
        }
    }

    @Entao("o sistema deve impedir a exclusao informando que o item esta em uso")
    public void oSistemaDeveImpedirAExclusao() {
        assertNotNull(contexto.excecaoCapturada);
        assertTrue(contexto.excecaoCapturada.getMessage().contains("alocado"));
    }
}