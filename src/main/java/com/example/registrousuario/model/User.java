package com.example.registrousuario.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    private String id;
    
    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String name;
    
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(regexp = "^[A-Za-z0-9+_.-]+@(.+)$", message = "El correo debe tener un formato válido")
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", 
            message = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial")
    @Column(nullable = false)
    private String password;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();
    
    private LocalDateTime created;
    
    private LocalDateTime modified;
    
    private LocalDateTime lastLogin;
    
    private String token;
    
    private boolean isActive;
    
    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.created = LocalDateTime.now();
        this.lastLogin = this.created;
        this.modified = this.created;
        this.isActive = true;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.modified = LocalDateTime.now();
    }
    
    public void addPhone(Phone phone) {
        phones.add(phone);
        phone.setUser(this);
    }
    
    public void removePhone(Phone phone) {
        phones.remove(phone);
        phone.setUser(null);
    }
} 