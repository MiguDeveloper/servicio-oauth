package pe.tuna.serviciooauth.services;

import pe.tuna.serviciousuariocommons.models.entity.Usuario;

public interface IUsuarioSerice {
    public Usuario findByUsername(String username);
    public Usuario update(Usuario usuario, Long id);
}
