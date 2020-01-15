package pe.tuna.serviciooauth.services;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.tuna.serviciooauth.clients.IUsuarioFeignClient;
import pe.tuna.serviciousuariocommons.models.entity.Usuario;

import java.util.List;
import java.util.stream.Collectors;

// Tenemos que indicar y configurar en spring security(en el authenticationManager) para indicar que el proceso
// de login se va hacer con esta implementación UsuarioService
@Service
public class UsuarioService implements IUsuarioSerice, UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private IUsuarioFeignClient client;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        try {
            Usuario usuario = client.findByUsername(username);
            List<GrantedAuthority> authorities = usuario.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getNombre()))
                    .peek(authority -> logger.info("ROL: " + authority.getAuthority()))
                    .collect(Collectors.toList());

            logger.info("Usuario autenticado: " + username);

            return new User(usuario.getUsername(), usuario.getPassword(), usuario.getEnabled(),
                    true, true, true, authorities);
        } catch (FeignException e) {
            logger.error("Error en el login, no existe el usuario '" + username + "' en sistema");
            throw new UsernameNotFoundException("Error en el login, no existe el usuario '" + username + "' en sistema");
        }
    }

    @Override
    public Usuario findByUsername(String username) {
        return client.findByUsername(username);
    }

    @Override
    public Usuario update(Usuario usuario, Long id) {
        return client.update(usuario, id);
    }
}
