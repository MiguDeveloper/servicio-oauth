package pe.tuna.serviciooauth.security.event;

import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pe.tuna.serviciooauth.services.IUsuarioSerice;
import pe.tuna.serviciousuariocommons.models.entity.Usuario;

/*
 * Tiene que ser un componente de spring para depues inyectarlo por lo tanto lo anotamos con Component
 * implementamos la interfaz con dos metodos para manejar el error y el success
 *
 * IMPORTANTE: registrar esta clase de evento en spring security
 */
@Component
public class AuthenticationSuccessErrorHandler implements AuthenticationEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationSuccessErrorHandler.class);

    @Autowired
    private IUsuarioSerice usuarioService;

    @Override
    public void publishAuthenticationSuccess(Authentication authentication) {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        String mensaje = "MIGUEL: Success login, " + user.getUsername();
        System.out.println(mensaje);
        logger.info(mensaje);

        // Como logra logearse debemos de reiniciar sus numero de intentos
        Usuario usuario = usuarioService.findByUsername(authentication.getName());
        // Si la propiedad en la clase fuera Integer usuario.getIntentos() != null &&
        if (usuario.getIntentos() != null && usuario.getIntentos() > 0) {
            usuario.setIntentos(0);
            usuarioService.update(usuario, usuario.getId());
        }
        usuario.setIntentos(0);
        usuarioService.update(usuario, usuario.getId());
    }

    @Override
    public void publishAuthenticationFailure(AuthenticationException e, Authentication authentication) {
        String mensaje = "MIGUEL: Error en el login, " + e.getMessage();
        System.out.println(mensaje);
        logger.error(mensaje);

        try {
            Usuario usuario = usuarioService.findByUsername(authentication.getName());

            if (usuario.getIntentos() == null) {
                usuario.setIntentos(0);
            }


            logger.info("Intentos actual es de: " + usuario.getIntentos());
            usuario.setIntentos(usuario.getIntentos() + 1);
            logger.info("Intentos despues es de: " + usuario.getIntentos());

            if (usuario.getIntentos() >= 3) {
                logger.error(String.format("El usuario %s deshabilitado por maximo numero de intentos.", usuario.getUsername()));
                usuario.setEnabled(false);
            }
            usuarioService.update(usuario, usuario.getId());

        } catch (FeignException ex) {
            logger.error(String.format("El usuario %s no existe en el sistema", authentication.getName()));
        }
    }
}
