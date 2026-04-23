package recifecultural.dominio.agenda.espaco;


import recifecultural.dominio.agenda.espaco.Espaco;
import recifecultural.dominio.agenda.espaco.IEspacoRepositorio;
import java.util.List;
import recifecultural.dominio.agenda.espaco.EspacoId;


public class EspacoServico {

    private final IEspacoRepositorio espacoRepositorio;


    public EspacoServico(IEspacoRepositorio espacoRepositorio) {
        this.espacoRepositorio = espacoRepositorio;
    }

    public EspacoId cadastrarEspaco(String nome, int capacidadeMaxima, List<String> riderTecnico) {
        Espaco novoEspaco = new Espaco(nome, capacidadeMaxima, riderTecnico);
        espacoRepositorio.salvar(novoEspaco);
        return novoEspaco.getId();
    }

    public void atualizarCapacidade(EspacoId espacoId, int novaCapacidade, int ingressosVendidosFuturos) {
        Espaco espaco = espacoRepositorio.obterPorId(espacoId)
                .orElseThrow(() -> new IllegalArgumentException("Espaço não encontrado."));

        // A regra de negócio está protegida dentro da Entidade
        espaco.reduzirCapacidade(novaCapacidade, ingressosVendidosFuturos);

        espacoRepositorio.atualizar(espaco);
    }

    public void interditarEspaco(EspacoId espacoId) {
        Espaco espaco = espacoRepositorio.obterPorId(espacoId)
                .orElseThrow(() -> new IllegalArgumentException("Espaço não encontrado."));

        espaco.interditar();
        espacoRepositorio.atualizar(espaco);

        // Aqui ocorreria o Evento de Domínio para notificar os artistas e alterar pautas:
        // eventoPublicador.publicar(new EspacoInterditadoEvent(espacoId));
    }
}