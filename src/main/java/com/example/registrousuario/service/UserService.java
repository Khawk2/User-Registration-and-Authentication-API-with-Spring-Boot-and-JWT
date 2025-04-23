package com.example.registrousuario.service;

import com.example.registrousuario.dto.PhoneDTO;
import com.example.registrousuario.dto.UserRequestDTO;
import com.example.registrousuario.dto.UserResponseDTO;
import com.example.registrousuario.exception.EmailAlreadyExistsException;
import com.example.registrousuario.model.Phone;
import com.example.registrousuario.model.User;
import com.example.registrousuario.repository.UserRepository;
import com.example.registrousuario.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Transactional
    public UserResponseDTO registerUser(UserRequestDTO userRequest) {
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new EmailAlreadyExistsException(userRequest.getEmail());
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));

        if (userRequest.getPhones() != null) {
            userRequest.getPhones().forEach(phoneDTO -> {
                Phone phone = new Phone();
                phone.setNumber(phoneDTO.getNumber());
                phone.setCitycode(phoneDTO.getCitycode());
                phone.setContrycode(phoneDTO.getContrycode());
                user.addPhone(phone);
            });
        }

        User savedUser = userRepository.save(user);

        // Crear el token después de guardar el usuario
        UserDetails userDetails = loadUserByUsername(savedUser.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );
        String token = tokenProvider.generateToken(authentication);
        savedUser.setToken(token);
        savedUser = userRepository.save(savedUser);

        return convertToDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO loginUser(String email, String password) {
        try {
            // Primero verificamos si el usuario existe
            User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Creamos el token de autenticación
            UserDetails userDetails = loadUserByUsername(email);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                password,
                userDetails.getAuthorities()
            );
            
            // Autenticamos
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            // Si la autenticación fue exitosa, generamos el token
            String token = tokenProvider.generateToken(authentication);
            user.setToken(token);
            user.setLastLogin(LocalDateTime.now());
            
            User updatedUser = userRepository.save(user);
            return convertToDTO(updatedUser);
        } catch (Exception e) {
            throw new RuntimeException("Credenciales inválidas");
        }
    }

    public UserResponseDTO getUserProfile(String token) {
        String email = tokenProvider.getUsernameFromJWT(token);
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return convertToDTO(user);
    }

    public UserResponseDTO getUserProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));
        return convertToDTO(user);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword()) // Ya está encriptado en la base de datos
            .authorities(new ArrayList<>())
            .build();
    }

    private UserResponseDTO convertToDTO(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setCreated(user.getCreated());
        dto.setModified(user.getModified());
        dto.setLastLogin(user.getLastLogin());
        dto.setToken(user.getToken());
        dto.setActive(user.isActive());
        
        if (user.getPhones() != null) {
            dto.setPhones(user.getPhones().stream()
                .map(this::convertToPhoneDTO)
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    private PhoneDTO convertToPhoneDTO(Phone phone) {
        PhoneDTO dto = new PhoneDTO();
        dto.setNumber(phone.getNumber());
        dto.setCitycode(phone.getCitycode());
        dto.setContrycode(phone.getContrycode());
        return dto;
    }
} 