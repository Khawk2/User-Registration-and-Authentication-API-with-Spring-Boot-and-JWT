# API de Registro y Autenticación de Usuarios

Este proyecto es una API REST desarrollada con Spring Boot para gestionar el registro y la autenticación de usuarios.

## Características

*   **Registro de Usuarios:** 
    - Registro con nombre, correo electrónico y contraseña
    - Validación de correos duplicados
    - Soporte opcional para números de teléfono
    - Encriptación de contraseñas con BCrypt

*   **Autenticación Segura:**
    - Autenticación mediante JWT (JSON Web Tokens)
    - Manejo de sesiones stateless
    - Expiración configurable de tokens
    - Validación robusta de tokens

*   **Gestión de Usuarios:**
    - Obtención de perfil de usuario
    - Validación de correos únicos
    - Registro de último acceso
    - Estado activo/inactivo de usuarios

*   **Seguridad:**
    - Protección contra ataques CSRF
    - Configuración CORS personalizable
    - Manejo seguro de secretos vía variables de entorno
    - Logging detallado de eventos de seguridad

*   **Documentación:**
    - API documentada con Swagger/OpenAPI 3
    - Endpoints anotados con descripciones detalladas
    - Ejemplos de respuestas y códigos de estado

## Tecnologías Utilizadas

*   Java 17
*   Spring Boot 3.x
*   Spring Security
*   JSON Web Tokens (JWT)
*   Spring Data JPA
*   H2 Database
*   Maven
*   Swagger/OpenAPI 3

## Configuración del Proyecto

### Requisitos Previos

*   JDK 17 o superior
*   Maven 3.6 o superior
*   Git

### Variables de Entorno

El proyecto requiere las siguientes variables de entorno:

```bash
# Requerido en producción - JWT Secret Key
export JWT_SECRET=tu-clave-secreta-muy-larga-y-segura

# Opcional - Puerto del servidor (por defecto: 8080)
export SERVER_PORT=8080
```

### Instalación y Ejecución

1.  **Clonar el Repositorio:**
    ```bash
    git clone <url-del-repositorio>
    cd registro-usuario
    ```

2.  **Configurar Variables de Entorno:**
    - Para desarrollo, puedes usar los valores por defecto
    - Para producción, asegúrate de configurar `JWT_SECRET`

3.  **Compilar el Proyecto:**
    ```bash
    mvn clean install
    ```

4.  **Ejecutar la Aplicación:**
    ```bash
    mvn spring-boot:run
    ```

## Endpoints de la API

### Autenticación

*   **Registro de Usuario**
    ```http
    POST /api/auth/register
    Content-Type: application/json

    {
      "name": "Usuario Ejemplo",
      "email": "usuario@ejemplo.com",
      "password": "contraseña123",
      "phones": [
        {
          "number": "123456789",
          "citycode": "1",
          "contrycode": "57"
        }
      ]
    }
    ```

*   **Inicio de Sesión**
    ```http
    POST /api/auth/login
    Content-Type: application/json

    {
      "email": "usuario@ejemplo.com",
      "password": "contraseña123"
    }
    ```

*   **Obtener Perfil**
    ```http
    GET /api/auth/profile
    Authorization: Bearer <tu-token-jwt>
    ```

## Seguridad

### JWT Configuration

El proyecto usa JWT para autenticación. La configuración se maneja a través de:

*   **application.properties:**
    ```properties
    jwt.secret=${JWT_SECRET:default-dev-only-secret}
    spring.security.jwt.jwtExpirationInMs=86400000
    ```

*   **Variables de Entorno:**
    - Desarrollo: Usa el valor por defecto (no seguro)
    - Producción: **DEBE** configurar `JWT_SECRET`

### Buenas Prácticas de Seguridad

1.  **Nunca subir al repositorio:**
    - Secretos JWT
    - Contraseñas
    - Credenciales de base de datos
    - Archivos .env

2.  **Usar en Producción:**
    - Secretos JWT fuertes (mínimo 256 bits)
    - HTTPS/SSL
    - Variables de entorno para configuración sensible

## Documentación de la API

La documentación completa de la API está disponible en:
```
http://localhost:8080/swagger-ui/index.html
```

## Contribución

1.  Fork el repositorio
2.  Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3.  Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4.  Push a la rama (`git push origin feature/AmazingFeature`)
5.  Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE.md](LICENSE.md) para más detalles. 