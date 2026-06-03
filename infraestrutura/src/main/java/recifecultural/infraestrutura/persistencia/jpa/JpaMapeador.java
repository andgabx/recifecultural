package recifecultural.infraestrutura.persistencia.jpa;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import recifecultural.infraestrutura.persistencia.agenda.evento.EventoMapeador;
import recifecultural.infraestrutura.persistencia.agenda.sorteio.SorteioMapeador;
import recifecultural.infraestrutura.persistencia.agenda.bloqueio.BloqueioAdministrativoMapeador;
import recifecultural.infraestrutura.persistencia.agenda.comentario.ComentarioMapeador;
import recifecultural.infraestrutura.persistencia.agenda.prereserva.PreReservaMapeador;
import recifecultural.infraestrutura.persistencia.agenda.acessibilidade.AcessibilidadeMapeador;
import recifecultural.infraestrutura.persistencia.artista.artista.ArtistaMapeador;
import recifecultural.infraestrutura.persistencia.artista.produtor.ProdutorMapeador;
import recifecultural.infraestrutura.persistencia.espaco.espaco.EspacoMapeador;
import recifecultural.infraestrutura.persistencia.espaco.setor.SetorMapeador;
import recifecultural.infraestrutura.persistencia.espaco.equipamento.EquipamentoMapeador;
import recifecultural.infraestrutura.persistencia.espaco.suporte.ChamadoSuporteMapeador;
import recifecultural.infraestrutura.persistencia.ingressos.IngressoMapeador;
import recifecultural.infraestrutura.persistencia.cupom.CupomMapeador;
import recifecultural.infraestrutura.persistencia.financeiro.FinanceiroMapeador;
import recifecultural.infraestrutura.persistencia.patrocinio.PatrocinioMapeador;
import recifecultural.infraestrutura.persistencia.catraca.CatracaMapeador;
import recifecultural.infraestrutura.persistencia.compartilhado.notificacao.NotificacaoMapeador;
import recifecultural.infraestrutura.persistencia.compartilhado.auditoria.AuditoriaMapeador;

@Component
public class JpaMapeador extends ModelMapper {

    public JpaMapeador() {
        EventoMapeador.registrar(this);
        SorteioMapeador.registrar(this);
        BloqueioAdministrativoMapeador.registrar(this);
        ComentarioMapeador.registrar(this);
        PreReservaMapeador.registrar(this);
        AcessibilidadeMapeador.registrar(this);
        ArtistaMapeador.registrar(this);
        ProdutorMapeador.registrar(this);
        EspacoMapeador.registrar(this);
        SetorMapeador.registrar(this);
        EquipamentoMapeador.registrar(this);
        ChamadoSuporteMapeador.registrar(this);
        IngressoMapeador.registrar(this);
        CupomMapeador.registrar(this);
        FinanceiroMapeador.registrar(this);
        PatrocinioMapeador.registrar(this);
        CatracaMapeador.registrar(this);
        NotificacaoMapeador.registrar(this);
        AuditoriaMapeador.registrar(this);
    }

    @Override
    public <D> D map(Object source, Class<D> destinationType) {
        return source != null ? super.map(source, destinationType) : null;
    }
}
