/* package com.example.demo.controlador;

import com.example.demo.datos.UsuarioDAO;
import com.example.demo.modelo.GeocodingService;
import com.example.demo.modelo.LatLng;
import com.example.demo.modelo.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@RestController
public class Controlador {

    @Autowired
    private UsuarioDAO usuarioDAO;

    @Autowired
    private GeocodingService geocodingService;


    @Value("${positionstack.api.key}")
    private String apiKey;


    @PostMapping(value = "demo")
    public String welcome() {
        return "Welcome form secure endpoint";
    }


    @GetMapping("/geocodificar")
    public ResponseEntity<String> geocodificarDireccion(@RequestParam String direccion) {
        String url = String.format("http://api.positionstack.com/v1/forward?access_key=%s&query=%s", apiKey, direccion);

        RestTemplate restTemplate = new RestTemplate();
        String resultado = restTemplate.getForObject(url, String.class);

        return ResponseEntity.ok(resultado);
    }

    // Obtener todos los usuarios
    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> usuarios() {
        List<Usuario> usuarios = usuarioDAO.getAllUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    // Crear un nuevo usuario
    @PostMapping("/crearUsuario/{nombre}/{nombreUsuario}/{contrasenia}/{direccion}/{ciudad}/{codigoPostal}/{pais}")
    public ResponseEntity<Usuario> crearUsuario(@PathVariable String nombre, @PathVariable String nombreUsuario, @PathVariable String contrasenia, @PathVariable String direccion, @PathVariable String ciudad, @PathVariable String codigoPostal, @PathVariable String pais) {
        // Llamada a la API de geocodificación
        LatLng latLng = geocodingService.obtenerLatitudLongitud(direccion + ", " + ciudad + ", " + codigoPostal + ", " + pais);

        if (latLng == null) {
            // Devolver un usuario con latitud y longitud nulas
            Usuario usuarioError = new Usuario();
            usuarioError.setNombre("Error al obtener la latitud y longitud");

            return new ResponseEntity<>(usuarioError, HttpStatus.BAD_REQUEST);
        }


        // Crear usuario con latitud y longitud
        Usuario usuario = new Usuario(nombre, nombreUsuario, contrasenia, direccion, ciudad, codigoPostal, pais, latLng.getLatitude(), latLng.getLongitude());

        // Guardar el usuario
        usuarioDAO.agregarUsuario(usuario);

        return new ResponseEntity<>(usuario, HttpStatus.CREATED);
    }
}
*/