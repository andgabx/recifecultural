package recifecultural.dominio.compartilhado.auditoria;

import java.util.List;

public interface IAuditoriaRepositorio {
    void registrar(RegistroAuditoria registro);
    List<RegistroAuditoria> listarRecentes(int limite);
    List<RegistroAuditoria> listarPorEntidade(String entidade, int limite);
}
