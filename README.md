#  Microcurriculo 

El proyecto se centra en el desarrollo de una aplicación de gestión de microcurrículos académicos para instituciones educativas. Esta aplicación tiene como objetivo principal proporcionar una plataforma integral para la creación, edición, seguimiento y aprobación de los microcurrículos utilizados en los programas académicos ofrecidos por la institución.
## Instalación y configuración 

Antes de empezar, asegúrate de tener instalados y configurados los siguientes elementos:

1. Java 17
2. Gradle 8.7
3. Base de datos PostgreSQL (con las variables de entorno USER y PASSWORD ya configuradas).

Para instalar y configurar la aplicación en un entorno local, sigue estos pasos:

1. Clona este repositorio en tu máquina local.
2. Importa el proyecto en tu IDE favorito.
3. Instala las dependencias del proyecto usando Gradle.
4. Crea una base de datos PostgreSQL con el nombre 'microcurriculo'.
5. Configura las variables de entorno necesarias para la base de datos.
    - Abre tu IDE y encuentra la configuración de ejecución para el 'microcurriculo'.
    - En el caso de IntelliJ IDEA, puedes hacer esto yendo a 'Run/Debug Configurations'.
    - Busca la configuración correspondiente y haz clic para editarla.
    - Dentro de la configuración, busca la sección 'Environment variables'.
    - Agrega las siguientes variables de entorno con los valores que hayas decidido para tu base de datos PostgreSQL:
        - USER: es el username que tienes en la base de datos.

      (En el caso de PgAdmin se encuentra en PostgreSQL->Properties->Connection->Username)

        - PASSWORD: la contraseña de la base de datos.
6. Para que funcione la mensajeria debes de crear un archivo llamado email.properties en la carpeta resources con los siguientes atributos:
```
email.username= <Correo de la institucion>
email.password= <Contraseña pedida del mismo correo en el apartado "Contraseñas de aplicacion" >
```
7. Ejecuta el proyecto con el bootRun.

## Endpoints de la API
Para obtener más información sobre los endpoints disponibles, consulta la documentación de la API.
Puedes importar la [colección de postman.](docs/postman/Micro.postman_collection.json) o 
acceder a la documentación de Swagger en `http://localhost:8080/swagger-ui/index.html`.


## FAQ
#### ¿Qué es este proyecto?
Este proyecto es una aplicación diseñada para gestionar los microcurrículos académicos en una institución educativa.

#### ¿Cuál es el propósito de esta aplicación?
El propósito principal de esta aplicación es facilitar la creación, modificación y seguimiento de los microcurrículos académicos por parte del personal docente y administrativo de la institución educativa.

#### ¿Qué características ofrece esta aplicación?
Creación y edición de microcurrículos académicos.
Seguimiento de cambios realizados por docentes.
Aprobación o rechazo de cambios por parte del director del programa.
Registro histórico de modificaciones en los microcurrículos.
Notificación de rechazo de cambios a los docentes.
#### ¿Quiénes pueden utilizar esta aplicación?
Esta aplicación está diseñada para ser utilizada por el personal docente y administrativo de la institución educativa, pero tambien se permiten a los visitantes visualizar los microcurriculos.

#### ¿Cómo se garantiza la seguridad de los datos en esta aplicación?
La aplicación implementa medidas de seguridad robustas, incluyendo autenticación de usuarios, control de acceso basado en roles y encriptación de datos sensibles.

#### ¿Pueden los docentes realizar cambios en los microcurrículos en cualquier momento?
No, los docentes pueden realizar cambios en los microcurrículos únicamente durante el período de modificación establecido previamente. Una vez finalizado este período, los cambios deben ser aprobados por el director del programa.

#### ¿Se pueden realizar cambios en los microcurrículos después de que comience el semestre?
No, los cambios en los microcurrículos deben ser realizados antes del inicio del semestre académico para garantizar la consistencia y planificación adecuada del plan de estudios.


## Contribución ##

Cualquier contribución al proyecto es bienvenida. Si deseas informar un error, solicitar una nueva funcionalidad o
contribuir con código, sigue estos pasos:

1. Crea un fork de este repositorio.
2. Crea una nueva rama con un nombre descriptivo.
3. Haz los cambios necesarios en esa rama.
4. Envía un pull request con una descripción clara y concisa de los cambios realizados.


## Comentario 
Si tienes algún comentario sobre el repositorio, por favor dímelo para poder mejorar :)

- 📫 Cómo contactarme **heinnervega20@gmail.com**