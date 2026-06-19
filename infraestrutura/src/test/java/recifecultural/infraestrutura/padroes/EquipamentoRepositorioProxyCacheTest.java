package recifecultural.infraestrutura.padroes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recifecultural.dominio.agenda.equipamento.Equipamento;
import recifecultural.dominio.agenda.equipamento.EquipamentoId;
import recifecultural.dominio.agenda.equipamento.IEquipamentoRepositorio;
import recifecultural.dominio.agenda.equipamento.StatusEquipamento;
import recifecultural.dominio.espaco.espaco.EspacoId;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipamentoRepositorioProxyCacheTest {

    @Mock
    private IEquipamentoRepositorio delegado;

    private EquipamentoRepositorioProxyCache proxy;

    private EspacoId espacoId;

    @BeforeEach
    void setUp() {
        proxy = new EquipamentoRepositorioProxyCache(delegado);
        espacoId = EspacoId.novo();
    }

    @Test
    void obterPorId_cache_miss_consulta_delegado() {
        EquipamentoId id = EquipamentoId.novo();
        Equipamento equipamento = new Equipamento(id, espacoId, "Projetor 4K", StatusEquipamento.DISPONIVEL, null);
        when(delegado.obterPorId(id)).thenReturn(Optional.of(equipamento));

        Optional<Equipamento> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(equipamento, resultado.get());
        verify(delegado, times(1)).obterPorId(id);
    }

    @Test
    void obterPorId_cache_hit_nao_chama_delegado() {
        EquipamentoId id = EquipamentoId.novo();
        Equipamento equipamento = new Equipamento(id, espacoId, "Mesa de som", StatusEquipamento.DISPONIVEL, null);
        when(delegado.obterPorId(id)).thenReturn(Optional.of(equipamento));

        proxy.obterPorId(id);
        Optional<Equipamento> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(equipamento, resultado.get());
        verify(delegado, times(1)).obterPorId(id);
    }

    @Test
    void salvar_popula_cache() {
        EquipamentoId id = EquipamentoId.novo();
        Equipamento equipamento = new Equipamento(id, espacoId, "Microfone condensador", StatusEquipamento.DISPONIVEL, null);

        proxy.salvar(equipamento);
        Optional<Equipamento> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(equipamento, resultado.get());
        verify(delegado, never()).obterPorId(id);
    }

    @Test
    void atualizar_invalida_cache() {
        EquipamentoId id = EquipamentoId.novo();
        Equipamento equipamentoOriginal = new Equipamento(id, espacoId, "Canhão de luz", StatusEquipamento.DISPONIVEL, null);
        Equipamento equipamentoAtualizado = new Equipamento(id, espacoId, "Canhão de luz v2", StatusEquipamento.DISPONIVEL, null);
        when(delegado.obterPorId(id))
                .thenReturn(Optional.of(equipamentoOriginal))
                .thenReturn(Optional.of(equipamentoAtualizado));

        proxy.obterPorId(id);
        proxy.atualizar(equipamentoOriginal);
        Optional<Equipamento> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(equipamentoAtualizado, resultado.get());
        verify(delegado, times(2)).obterPorId(id);
        verify(delegado, times(1)).atualizar(equipamentoOriginal);
    }

    @Test
    void deletar_remove_do_cache() {
        EquipamentoId id = EquipamentoId.novo();
        Equipamento equipamento = new Equipamento(id, espacoId, "Cabine acústica", StatusEquipamento.DISPONIVEL, null);
        when(delegado.obterPorId(id))
                .thenReturn(Optional.of(equipamento))
                .thenReturn(Optional.empty());

        proxy.obterPorId(id);
        proxy.deletar(id);
        Optional<Equipamento> resultado = proxy.obterPorId(id);

        assertFalse(resultado.isPresent());
        verify(delegado, times(2)).obterPorId(id);
        verify(delegado, times(1)).deletar(id);
    }
}
