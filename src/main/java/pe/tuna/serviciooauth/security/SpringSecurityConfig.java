package pe.tuna.serviciooauth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    // Aqui inyectamos la clase service de autenticacion que hemos implementado
    @Autowired
    private UserDetailsService usuarioService;

    // lo que retorna el metodo es lo que se va a registrar como beans de spring
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Ahora registramos el 'usuarioService' userDetailService en el authenticationManager
    // lo anotamos con autowired para inyectarlo por metodo
    @Override
    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usuarioService).passwordEncoder(passwordEncoder());
    }

    // Configuramos el authentication manager y lo tenemos que registrar tambien como componente de spring para luego
    // inyectarlo en la configuracion del servidor de autenticacion Oauth2 por eso lo anotamos con @Bean
    @Override
    @Bean
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
