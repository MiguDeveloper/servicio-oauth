package pe.tuna.serviciooauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.stereotype.Component;
import pe.tuna.serviciooauth.services.IUsuarioSerice;
import pe.tuna.serviciousuariocommons.models.entity.Usuario;

import java.util.HashMap;
import java.util.Map;

/*
 * Lo que haremos con este componente es agregar mas informacion necesaria al token de autenticacion
 */
@Component
public class InfoAdicionalToken implements TokenEnhancer {


    @Autowired
    private IUsuarioSerice usuarioSerice;


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication oAuth2Authentication) {
        Map<String, Object> info = new HashMap<String, Object>();

        Usuario usuario = usuarioSerice.findByUsername(oAuth2Authentication.getName());
        info.put("nombre", usuario.getNombre());
        info.put("apellido", usuario.getApellido());
        info.put("correo", usuario.getEmail());
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(info);

        return accessToken;
    }
}
