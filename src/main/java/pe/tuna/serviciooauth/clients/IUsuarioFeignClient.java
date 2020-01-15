package pe.tuna.serviciooauth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import pe.tuna.serviciousuariocommons.models.entity.Usuario;

// Recordemos que para que sea un cliente feign tenemos que poner la anotacion FeignClient
@FeignClient(name = "servicio-usuarios")
public interface IUsuarioFeignClient {

    @GetMapping("/usuarios/search/buscar-username")
    public Usuario findByUsername(@RequestParam String username);

    @PutMapping("/usuarios/{id}")
    public Usuario update(@RequestBody Usuario usuario, @PathVariable Long id);
}
