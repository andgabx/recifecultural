package recifecultural.dominio.agenda.espaco;

import java.time.LocalDateTime;

public record Ocupacao(
        LocalDateTime inicio,
        LocalDateTime fim,
        int minutosMontagem,
        int minutosDesmontagem,
        int bufferExtra
) {
    public LocalDateTime inicioEfetivo() {
        return inicio.minusMinutes(minutosMontagem);
    }

    public LocalDateTime fimEfetivo() {
        return fim.plusMinutes(minutosDesmontagem).plusMinutes(bufferExtra);
    }

    public boolean sobrepoe(Ocupacao outra) {
        return this.inicioEfetivo().isBefore(outra.fimEfetivo()) &&
                outra.inicioEfetivo().isBefore(this.fimEfetivo());
    }
}