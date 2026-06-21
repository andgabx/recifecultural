package recifecultural.dominio.espaco.suporte;

import recifecultural.dominio.espaco.setor.Assento;
import recifecultural.dominio.espaco.espaco.EspacoId;

public interface INotificadorSuporte {
    void notificarAbertura(ChamadoSuporte chamado, Assento assento, EspacoId espacoId);
}