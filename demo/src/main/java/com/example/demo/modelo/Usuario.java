package com.example.demo.modelo;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Usuario", uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "NOMBRE")
    private String nombre;

    @Column(name = "USERNAME", nullable = false)
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "FOTO_PERFIL")
    private String fotoPerfil;

    @Column(name = "DIRECCION")
    private String direccion;

    @Column(name = "CIUDAD")
    private String ciudad;

    @Column(name = "CODIGO_POSTAL")
    private String codigoPostal;

    @Column(name = "PAIS")
    private String pais;

    @Column(name = "LATITUD")
    private double latitud;

    @Column(name = "LONGITUD")
    private double longitud;

    @Enumerated(EnumType.STRING)
    Role role;

    @JsonManagedReference
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Tarjeta> tarjetas;

    @Column(name = "SANCIONES_DISPONIBLES")
    private int sancionesDisponibles = 2; // Máximo 2 sanciones por cuenta

    @Column(name = "ENABLED")
    private boolean enabled = true; // Estado de la cuenta (activa/inactiva)

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null) {
            return List.of(new SimpleGrantedAuthority("USER"));
        }
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}

