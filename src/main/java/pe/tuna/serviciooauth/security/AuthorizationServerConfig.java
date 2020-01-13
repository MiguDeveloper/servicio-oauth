package pe.tuna.serviciooauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.Arrays;

/*
 * EnableAuthorizationServer: Habilitamos la clase como un servidor de Autorizacion
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private InfoAdicionalToken infoAdicionalToken;

    /*
     * Es el permiso que van a tener nuestros endpoints del servidor de configuracion de OAUTH2 para generar y tambien
     * validar el token
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    /*
     * Configuramos las aplicaciones clientes, si deseamos agregar una app mas podemos agregar con:
     * .and().
     * withClient(...
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("frontendapp")
                .secret(passwordEncoder.encode("12345"))
                .scopes("read", "write")
                .authorizedGrantTypes("password", "refresh_token")
                .accessTokenValiditySeconds(3600)
                .refreshTokenValiditySeconds(3600);
    }

    /*
     * Aqui es donde configuramos el authenticationManager y tambien el tokenStororage de tipo JWT
     * y el accesTokenConverter que se encarga de guardar los datos del user en el token por ejemplo: roles, nombre
     * apellidos, etc. y el accessTokenConverter los convierte en el TOKEN
     *
     * IMPORTANTE: recordar que el endpoint esta relacionado al del OAUTH2 del servidor de autorizacion encargado de
     * generar el token le endPoint es '/oauth/token' del tipo POST
     *
     * Aquí tambien es donde unimos la informacion adicional de token que es el accessTokenConverter para ello usamos
     * la clase tokenEnhancerChange
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(infoAdicionalToken, accessTokenConverter()));

        endpoints.authenticationManager(authenticationManager)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain);
    }

    /*
     * TokenStore: se encarga de generar y almacenar el token con los datos de accessTokenConverter(este es el encargado
     * de convertir en JsonWebToken con todos los datos: username, roles , etc)
     */
    @Bean
    public JwtTokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    /*
     * En este metodo debemos de agregar el codigo secreto para firmar el token, el cual se usara en el servidor de recursos
     * para validar el token y dar permiso a nuestros recursos protegidos
     */
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter tokenConverter = new JwtAccessTokenConverter();
        tokenConverter.setSigningKey("algun_codigo_secreto_aeiou");
        return tokenConverter;
    }
}
