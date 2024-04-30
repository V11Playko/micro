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
    public static final String CREATED_MESSAGE = "Creado satisfactoriamente.";
    public static final String UPDATED_MESSAGE = "Actualizado satisfactoriamente.";
    public static final String DELETED_MESSAGE = "Eliminado satisfactoriamente.";
    public static final String PAGINA_ILEGAL_MESSAGE = "El número de página debe ser mayor o igual a 1.";
    public static final String PROGRAMA_NOT_FOUND_MESSAGE = "El programa academico no existe.";
    public static final String DIRECTOR_NOT_FOUND_MESSAGE = "El director no existe.";
    public static final String DIRECTOR_ALREADY_ASSIGN_MESSAGE = "El usuario ya es director de otro programa académico.";
    public static final String DURACION_INVALIDA_MESSAGE = "La duracion del periodo de modificacion no se puedo modificar";
    public static final String PERIODO_MODIFICACION_INVALIDO_MESSAGE = "El periodo de modificacion no se pudo modificar.";
    public static final String PROGRAMA_EXISTENTE_MESSAGE = "El programa académico ya existe";
    public static final String DOCENTE_NOT_FOUND_MESSAGE= "El docente no existe";
    public static final String ALL_ASIGNATURAS_ASSIGNS_MESSAGE= "Algunas asignaturas ya han sido asignadas a este pensum.";
    public static final String ASIGNATURA_NOT_FOUND_MESSAGE= "Asignatura no existe.";
    public static final String PENSUM_NOT_FOUND_MESSAGE= "Pensum no existe.";
    public static final String ALL_DOCENTES_ASSIGNS_MESSAGE= "Todos los docentes ya han sido asignadas a esta asignatura.";
    public static final String DOCENTE_NOT_ASSIGN_MESSAGE= "El docente no esta asignado a esta asignatura.";
    public static final String AREA_FORMACION_NOT_FOUND_MESSAGE= "Esa area de formacion no existe.";
    public static final String PRE_REQUISITO_NOT_FOUND_MESSAGE= "Ese pre-requisito no existe.";
    public static final String TEMAS_NOT_FOUND_MESSAGE= "Los temas no existen.";
    public static final String UNIDAD_NOT_FOUND_MESSAGE= "La unidad no existe.";
    public static final String RESULTADO_APRENDIZAJE_NOT_FOUND= "El resultado de aprendizaje no existe.";
    public static final String COMPETENCIA_NOT_FOUND_MESSAGE= "La competencia no existe";
    public static final String FAKE_ESTATUS_NOT_ALLOWED= "No se pudo asignar, ya que resultado de aprendizaje o competencia tienen estatus falso.";
    public static final String ESTATUS_FAKE_PENSUM_MESSAGE= "El estatus del pensum es false.";
}
