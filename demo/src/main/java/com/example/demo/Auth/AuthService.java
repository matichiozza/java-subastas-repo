package com.example.demo.Auth;

import com.example.demo.datos.UsuarioRepository;
import com.example.demo.jwt.JwtService;
import com.example.demo.modelo.Role;
import com.example.demo.modelo.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserDetails user=usuarioRepository.findByUsername(request.getUsername()).orElseThrow();
        String token=jwtService.getToken(user);
        return AuthResponse.builder()
                .token(token)
                .build();

    }

    public AuthResponse register(RegisterRequest request) {
        // Convertir latitud y longitud de String a double
        double latitud = 0.0;
        double longitud = 0.0;
        
        try {
            if (request.getLatitud() != null && !request.getLatitud().isEmpty()) {
                latitud = Double.parseDouble(request.getLatitud());
            }
            if (request.getLongitud() != null && !request.getLongitud().isEmpty()) {
                longitud = Double.parseDouble(request.getLongitud());
            }
        } catch (NumberFormatException e) {
            // Si no se puede convertir, usar valores por defecto
            latitud = 0.0;
            longitud = 0.0;
        }

        Usuario usuario = Usuario.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Si usas un encoder
                .nombre(request.getNombre())
                .direccion(request.getDireccion())
                .latitud(latitud)
                .longitud(longitud)
                .role(Role.USER)
                .sancionesDisponibles(2) // Usuarios nuevos empiezan con 2 sanciones
                .enabled(true) // Cuenta activa por defecto
                .build();

        usuarioRepository.save(usuario);

        return AuthResponse.builder()
                .token(jwtService.getToken(usuario))
                .build();
    }

}
