package recifecultural.dominio.ingressos;

import java.util.UUID;

public interface IConfirmacaoReserva {
    void confirmar(UUID preReservaId);
    void cancelar(UUID preReservaId);
}
