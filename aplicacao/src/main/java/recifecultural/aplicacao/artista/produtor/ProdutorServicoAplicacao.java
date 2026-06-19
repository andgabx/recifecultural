package recifecultural.aplicacao.artista.produtor;

import recifecultural.dominio.artista.produtor.Cnpj;
import recifecultural.dominio.artista.produtor.ProdutorId;
import recifecultural.dominio.artista.produtor.ProdutorServico;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import static org.apache.commons.lang3.Validate.notNull;

@Transactional(readOnly = true)
public class ProdutorServicoAplicacao {

    private final ProdutorServico servico;
    private final ProdutorRepositorioAplicacao repositorio;

    public ProdutorServicoAplicacao(ProdutorServico servico, ProdutorRepositorioAplicacao repositorio) {
        notNull(servico, "ProdutorServico não pode ser nulo.");
        notNull(repositorio, "ProdutorRepositorioAplicacao não pode ser nulo.");
        this.servico = servico;
        this.repositorio = repositorio;
    }

    public List<ProdutorResumo> pesquisarResumos() {
        return repositorio.pesquisarResumos();
    }

    @Transactional
    public ProdutorId cadastrar(String nomeFantasia, Cnpj cnpj, String email, String telefone) {
        return servico.cadastrar(nomeFantasia, cnpj, email, telefone);
    }

    @Transactional
    public void suspender(ProdutorId id, String responsavel, String motivo) {
        servico.suspender(id, responsavel, motivo);
    }

    @Transactional
    public void reativar(ProdutorId id, String responsavel, String motivo) {
        servico.reativar(id, responsavel, motivo);
    }

    @Transactional
    public void inativar(ProdutorId id, String responsavel, String motivo) {
        servico.inativar(id, responsavel, motivo);
    }
}
