package com.micro.demo.configuration;

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SWAGGER_TITLE_MESSAGE = "User API";
    public static final String SWAGGER_DESCRIPTION_MESSAGE = "User microservice";
    public static final String SWAGGER_VERSION_MESSAGE = "1.0.0";
    public static final String SWAGGER_LICENSE_NAME_MESSAGE = "Apache 2.0";
    public static final String SWAGGER_LICENSE_URL_MESSAGE = "http://springdoc.org";
    public static final String SWAGGER_TERMS_OF_SERVICE_MESSAGE = "http://swagger.io/terms/";
    public static final String RESPONSE_MESSAGE_KEY = "Mensaje";
    public static final String USER_CREATED_MESSAGE = "Usuario creado satisfactoriamente.";
    public static final String USER_UPDATED_MESSAGE = "Usuario actualizado satisfactoriamente.";
    public static final String USER_DELETED_MESSAGE = "Usuario eliminado satisfactoriamente.";
    public static final String NO_DATA_FOUND_MESSAGE = "Datos no encontrados";
    public static final String UNAUTHORIZED_MESSAGE = "No tienes permiso para acceder a este recurso.";
    public static final String USER_NOT_FOUND_UNAUTHORIZED_MESSAGE = "No se encontró un usuario autenticado.";
    public static final String USER_ALREADY_EXISTS_MESSAGE = "Este correo ya esta en uso.";
    public static final String ROLE_NOT_FOUND_MESSAGE = "Este role no existe.";
}
