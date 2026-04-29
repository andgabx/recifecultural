package recifecultural.dominio.agenda.bdd;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.StatusEspaco;
import recifecultural.dominio.espaco.setor.Setor;
import recifecultural.dominio.espaco.setor.SetorId;
import recifecultural.dominio.espaco.setor.TipoSetor;
import recifecultural.dominio.espaco.setor.ISetorRepositorio;
import recifecultural.dominio.espaco.setor.GestaoAmbienteInternoServico;
import recifecultural.dominio.espaco.setor.Assento;
import recifecultural.dominio.espaco.setor.MotivoIndisponibilidadeAssento;
import recifecultural.dominio.espaco.setor.StatusAssento;
import recifecultural.dominio.agenda.prereserva.PreReservaServico;
import recifecultural.dominio.agenda.prereserva.PreReserva;
import recifecultural.dominio.agenda.prereserva.PreReservaId;
import recifecultural.dominio.agenda.prereserva.DuracaoPreReserva;
import recifecultural.dominio.agenda.prereserva.IPreReservaRepositorio;
import recifecultural.dominio.agenda.prereserva.ConcorrenciaException;
import recifecultural.dominio.agenda.prereserva.StatusPreReserva;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.time.LocalDateTime;

public class PassosMapeamentoAssentos {

    @Mock private IEspacoRepositorio espacoRepositorio;
    @Mock private ISetorRepositorio setorRepositorio;
    @Mock private IPreReservaRepositorio preReservaRepositorio;

    private GestaoAmbienteInternoServico setorServico;
    private PreReservaServico preReservaServico;
    
    private Espaco espacoMock;
    private Setor setorConfigurado;
    private Exception excecaoLancada;

    private Assento assentoMock;
    private Setor setorMock;
    private UUID setorIdMock = UUID.randomUUID();
    private UUID assentoIdMock = UUID.randomUUID();
    
    public PassosMapeamentoAssentos() {
        MockitoAnnotations.openMocks(this);
        setorServico = new GestaoAmbienteInternoServico(setorRepositorio, espacoRepositorio);
        preReservaServico = new PreReservaServico(preReservaRepositorio, setorRepositorio);
    }

    @Dado("que o espaço {string} possui status {string}")
    public void queOEspaçoPossuiStatus(String nome, String status) {
        espacoMock = mock(Espaco.class);
        when(espacoMock.getNome()).thenReturn(nome);
        when(espacoMock.getStatus()).thenReturn(StatusEspaco.valueOf(status));
        when(espacoRepositorio.obterPorId(any(EspacoId.class))).thenReturn(Optional.of(espacoMock));
    }

    @Quando("eu configuro um setor com {int} fileiras e {int} assentos por fileira")
    public void euConfiguroUmSetorComFileirasEAssentosPorFileira(int fileiras, int assentos) {
        setorConfigurado = setorServico.configurarGestaoAmbiente(EspacoId.novo(), "Setor 1", TipoSetor.PLATEIA, fileiras, assentos);
    }

    @Então("a grade gerada deve conter os assentos {string}, {string}, {string}, {string}")
    public void aGradeGeradaDeveConterOsAssentos(String a1, String a2, String b1, String b2) {
        List<String> codigosGerados = setorConfigurado.getAssentos().stream().map(Assento::getCodigo).toList();
        assertTrue(codigosGerados.contains(a1));
        assertTrue(codigosGerados.contains(a2));
        assertTrue(codigosGerados.contains(b1));
        assertTrue(codigosGerados.contains(b2));
    }

    @E("os códigos dos assentos devem ser únicos no setor")
    public void osCódigosDosAssentosDevemSerÚnicosNoSetor() {
        long total = setorConfigurado.getAssentos().size();
        long unicos = setorConfigurado.getAssentos().stream().map(Assento::getCodigo).distinct().count();
        assertEquals(total, unicos);
    }

    @Dado("que o assento {string} está {string}")
    public void queOAssentoEstá(String codigoAssento, String status) {
        assentoMock = new Assento(assentoIdMock, codigoAssento, "A", 1, StatusAssento.valueOf(status), MotivoIndisponibilidadeAssento.OUTRO, 0);
        setorMock = new Setor(SetorId.de(setorIdMock.toString()), EspacoId.novo(), "Setor 1", TipoSetor.PLATEIA, 10, 10, List.of(assentoMock), 0);
        when(setorRepositorio.obterPorId(any(SetorId.class))).thenReturn(Optional.of(setorMock));
        when(preReservaRepositorio.listarAtivasPorAssento(any(UUID.class))).thenReturn(new ArrayList<>());
    }

    @Quando("o usuário {string} e o usuário {string} tentam pré-reservar o assento {string} simultaneamente")
    public void oUsuárioEOUsuárioTentamPréReservarOAssentoSimultaneamente(String user1, String user2, String assento) {
        UUID u1Id = UUID.randomUUID();
        UUID u2Id = UUID.randomUUID();

        doAnswer(new Answer<Void>() {
            private int count = 0;
            public Void answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                if (count++ > 0) {
                    throw new ConcorrenciaException("Optimistic lock exception simulada");
                }
                return null;
            }
        }).when(setorRepositorio).atualizar(any(Setor.class));

        try {
            preReservaServico.reservar(setorIdMock, assentoIdMock, u1Id, new DuracaoPreReserva(java.time.Duration.ofMinutes(10)));
            setorMock.liberarAssento(assentoIdMock);
            preReservaServico.reservar(setorIdMock, assentoIdMock, u2Id, new DuracaoPreReserva(java.time.Duration.ofMinutes(10)));
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Então("o sistema deve processar a primeira reserva com sucesso")
    public void oSistemaDeveProcessarAPrimeiraReservaComSucesso() {
        verify(setorRepositorio, atLeastOnce()).atualizar(any(Setor.class));
    }

    @E("o sistema deve lançar {string} para a segunda tentativa")
    public void oSistemaDeveLançarParaASegundaTentativa(String exceptionName) {
        assertNotNull(excecaoLancada);
        assertEquals(exceptionName, excecaoLancada.getClass().getSimpleName());
    }

    @Dado("que o assento {string} tem uma pré-reserva expirada no passado")
    public void queOAssentoTemUmaPréReservaExpiradaNoPassado(String assentoCodigo) {
        assentoMock = new Assento(assentoIdMock, assentoCodigo, "B", 2, StatusAssento.PRE_RESERVADO, MotivoIndisponibilidadeAssento.OUTRO, 0);
        setorMock = new Setor(SetorId.de(setorIdMock.toString()), EspacoId.novo(), "Setor 1", TipoSetor.PLATEIA, 10, 10, List.of(assentoMock), 0);
        
        PreReserva pr = new PreReserva(PreReservaId.novo(), assentoIdMock, setorIdMock, UUID.randomUUID(), LocalDateTime.now().minusMinutes(20), LocalDateTime.now().minusMinutes(10), StatusPreReserva.ATIVA, 0);
        when(preReservaRepositorio.listarAtivasExpiradas(any(LocalDateTime.class))).thenReturn(List.of(pr));
        when(setorRepositorio.obterPorId(any(SetorId.class))).thenReturn(Optional.of(setorMock));
    }

    @Quando("o job de expiração de reservas for executado")
    public void oJobDeExpiraçãoDeReservasForExecutado() {
        preReservaServico.expirarVencidas();
    }

    @Então("o status do assento {string} deve retornar para {string}")
    public void oStatusDoAssentoDeveRetornarPara(String assentoCodigo, String status) {
        assertEquals(StatusAssento.valueOf(status), assentoMock.getStatus());
    }

    private PreReserva prAtiva;

    @Dado("que o assento {string} tem uma pré-reserva {string} para o usuário {string}")
    public void queOAssentoTemUmaPréReservaParaOUsuário(String assentoCodigo, String status, String usuario) {
        assentoMock = new Assento(assentoIdMock, assentoCodigo, "B", 2, StatusAssento.PRE_RESERVADO, MotivoIndisponibilidadeAssento.OUTRO, 0);
        setorMock = new Setor(SetorId.de(setorIdMock.toString()), EspacoId.novo(), "Setor 1", TipoSetor.PLATEIA, 10, 10, List.of(assentoMock), 0);
        
        prAtiva = new PreReserva(PreReservaId.novo(), assentoIdMock, setorIdMock, UUID.randomUUID(), LocalDateTime.now().minusMinutes(5), LocalDateTime.now().plusMinutes(5), StatusPreReserva.valueOf(status), 0);
        when(preReservaRepositorio.obterPorId(prAtiva.getId())).thenReturn(Optional.of(prAtiva));
        when(setorRepositorio.obterPorId(any(SetorId.class))).thenReturn(Optional.of(setorMock));
    }

    @Quando("o usuário {string} cancelar a pré-reserva")
    public void oUsuárioCancelarAPréReserva(String usuario) {
        preReservaServico.cancelar(prAtiva.getId());
    }

    @Então("o status do assento {string} deve retornar para {string} atomicamente")
    public void oStatusDoAssentoDeveRetornarParaAtomicamente(String assentoCodigo, String status) {
        assertEquals(StatusAssento.valueOf(status), assentoMock.getStatus());
        assertEquals(StatusPreReserva.CANCELADA, prAtiva.getStatus());
    }

    @Quando("o usuário {string} tenta pré-reservar o assento {string}")
    public void oUsuarioTentaPreReservarOAssento(String usuario, String assentoCodigo) {
        try {
            preReservaServico.reservar(setorIdMock, assentoIdMock, UUID.randomUUID(), new DuracaoPreReserva(java.time.Duration.ofMinutes(10)));
        } catch (Exception e) {
            excecaoLancada = e;
        }
    }

    @Então("o sistema deve lançar um erro de assento não disponível")
    public void oSistemaDeveLancarUmErroDeAssentoNaoDisponivel() {
        assertNotNull(excecaoLancada);
        assertTrue(excecaoLancada instanceof IllegalStateException);
        assertTrue(excecaoLancada.getMessage().contains("não está disponível"));
    }

    @Quando("o administrador bloquear o assento {string}")
    public void oAdministradorBloquearOAssento(String assentoCodigo) {
        setorMock.bloquearAssento(assentoIdMock, MotivoIndisponibilidadeAssento.OUTRO);
    }

    @Quando("a compra for confirmada e o assento {string} for ocupado")
    public void aCompraForConfirmadaEOAssentoForOcupado(String assentoCodigo) {
        setorMock.ocuparAssento(assentoIdMock);
    }
}
