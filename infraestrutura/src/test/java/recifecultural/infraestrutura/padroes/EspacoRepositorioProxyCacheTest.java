package recifecultural.infraestrutura.padroes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import recifecultural.dominio.espaco.espaco.Espaco;
import recifecultural.dominio.espaco.espaco.EspacoId;
import recifecultural.dominio.espaco.espaco.IEspacoRepositorio;
import recifecultural.dominio.espaco.espaco.StatusEspaco;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EspacoRepositorioProxyCacheTest {

    @Mock
    private IEspacoRepositorio delegado;

    private EspacoRepositorioProxyCache proxy;

    @BeforeEach
    void setUp() {
        proxy = new EspacoRepositorioProxyCache(delegado);
    }

    @Test
    void obterPorId_cache_miss_consulta_delegado() {
        EspacoId id = EspacoId.novo();
        Espaco espaco = new Espaco(id, "Teatro Santa Isabel", 800, List.of(), StatusEspaco.ATIVO);
        when(delegado.obterPorId(id)).thenReturn(Optional.of(espaco));

        Optional<Espaco> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(espaco, resultado.get());
        verify(delegado, times(1)).obterPorId(id);
    }

    @Test
    void obterPorId_cache_hit_nao_chama_delegado_segunda_vez() {
        EspacoId id = EspacoId.novo();
        Espaco espaco = new Espaco(id, "Cinema São Luiz", 300, List.of(), StatusEspaco.ATIVO);
        when(delegado.obterPorId(id)).thenReturn(Optional.of(espaco));

        proxy.obterPorId(id);
        Optional<Espaco> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(espaco, resultado.get());
        verify(delegado, times(1)).obterPorId(id);
    }

    @Test
    void salvar_popula_cache() {
        EspacoId id = EspacoId.novo();
        Espaco espaco = new Espaco(id, "Cais do Sertão", 500, List.of(), StatusEspaco.ATIVO);

        proxy.salvar(espaco);
        Optional<Espaco> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(espaco, resultado.get());
        verify(delegado, never()).obterPorId(id);
    }

    @Test
    void atualizar_invalida_cache_e_redelegado_na_proxima_chamada() {
        EspacoId id = EspacoId.novo();
        Espaco espacoOriginal = new Espaco(id, "Armazém do Campo", 200, List.of(), StatusEspaco.ATIVO);
        Espaco espacoAtualizado = new Espaco(id, "Armazém do Campo", 150, List.of(), StatusEspaco.ATIVO);
        when(delegado.obterPorId(id))
                .thenReturn(Optional.of(espacoOriginal))
                .thenReturn(Optional.of(espacoAtualizado));

        proxy.obterPorId(id);
        proxy.atualizar(espacoOriginal);
        Optional<Espaco> resultado = proxy.obterPorId(id);

        assertTrue(resultado.isPresent());
        assertSame(espacoAtualizado, resultado.get());
        verify(delegado, times(2)).obterPorId(id);
        verify(delegado, times(1)).atualizar(espacoOriginal);
    }

    @Test
    void obterPorId_vazio_nao_e_cacheado() {
        EspacoId id = EspacoId.novo();
        when(delegado.obterPorId(id)).thenReturn(Optional.empty());

        Optional<Espaco> primeiraConsulta = proxy.obterPorId(id);
        Optional<Espaco> segundaConsulta = proxy.obterPorId(id);

        assertFalse(primeiraConsulta.isPresent());
        assertFalse(segundaConsulta.isPresent());
        verify(delegado, times(2)).obterPorId(id);
    }
}
