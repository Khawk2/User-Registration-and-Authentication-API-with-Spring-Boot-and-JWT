package com.example.registrousuario.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("El correo " + email + " ya está registrado");
    }
} 