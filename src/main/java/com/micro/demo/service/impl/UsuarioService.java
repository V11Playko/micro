package com.micro.demo.service.impl;

import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.entities.Role;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IRoleRepository;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IAuthPasswordEncoderPort;
import com.micro.demo.service.IUsuarioService;
import com.micro.demo.service.exceptions.IlegalPaginaException;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.RoleNotFoundException;
import com.micro.demo.service.exceptions.UnauthorizedException;
import com.micro.demo.service.exceptions.UserAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UsuarioService implements IUsuarioService {
    private final IUsuarioRepository usuarioRepository;
    private final IRoleRepository roleRepository;
    private final IAuthPasswordEncoderPort passwordEncoderPort;

    public UsuarioService(IUsuarioRepository usuarioRepository, IRoleRepository roleRepository, IAuthPasswordEncoderPort passwordEncoderPort) {
        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    /**
     * Obtiene los usuarios mediante paginacion, el director solo podra ver usuarios docentes.
     *
     * @param pagina numero de pagina
     * @param elementosXpagina elementos que habran en cada pagina
     * @return Lista de usuarios.
     * @throws IlegalPaginaException - Si el numero de pagina es menor a 1
     * @throws NoDataFoundException - Si no se encuentra datos.
     */
    @Override
    public List<Usuario> getAllUsers(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IlegalPaginaException();
        }

        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        Usuario usuarioAutenticado = getUserByCorreo(correoUsuarioAutenticado);

        Pageable pageable = PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending());
        Page<Usuario> page;

        if (usuarioAutenticado != null && usuarioAutenticado.getRole().getNombre().equals("ROLE_DIRECTOR")) {
            page = usuarioRepository.findByRoleNombre("ROLE_DOCENTE", pageable);
        } else {
            page = usuarioRepository.findAll(pageable);
        }

        if (page.isEmpty()) {
            throw new NoDataFoundException();
        }

        return page.getContent();
    }

    /**
     * Obtener un usuario por su correo
     *
     * @param correo - Correo del usuario
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     **/
    @Override
    public Usuario getUserByCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo);
        if (usuario == null) throw new NoDataFoundException();
        return usuario;
    }

    /**
     * Guardar un usuario
     *
     * @param usuario - Informacion del usuario.
     * @param roleName - Nombre del role que se le asignara al usuario
     * @throws UserAlreadyExistsException - Se lanza si ya existe un usuario con ese correo.
     * @throws RoleNotFoundException - Se lanza si no se encuentra el Role que pusiste en el parametro roleName
     * */
    @Override
    public void saveUser(Usuario usuario, String roleName) {
        String correo = usuario.getCorreo();
        if (usuarioRepository.existsByCorreo(correo)) {
            throw new UserAlreadyExistsException();
        }
        Role role = roleRepository.findByNombre(roleName);
        if (role == null) {
            throw new RoleNotFoundException();
        }
        usuario.setRole(role);
        usuario.setContraseña(passwordEncoderPort.encodePassword(usuario.getContraseña()));
        usuarioRepository.save(usuario);
    }

    /**
     * Actualizar un usuario
     *
     * @param id - Identificador unico del usuario a actualizar.
     * @param usuarioActualizado - Informacion del usuario.
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     **/
    @Override
    public void updateUser(Long id, Usuario usuarioActualizado) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(NoDataFoundException::new);
        Usuario usuarioAutenticado = getUserByCorreo(correoUsuarioAutenticado);

        boolean isAdminOrOwnUser = (correoUsuarioAutenticado != null &&
                (correoUsuarioAutenticado.equals("edu.ufps10@gmail.com") ||
                        correoUsuarioAutenticado.equals(usuarioExistente.getCorreo())));

        boolean isDirectorUpdatingDocente = (usuarioAutenticado.getRole().getNombre().equals("ROLE_DIRECTOR") &&
                usuarioExistente.getRole().getNombre().equals("ROLE_DOCENTE"));

        if (isAdminOrOwnUser || isDirectorUpdatingDocente) {
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setApellido(usuarioActualizado.getApellido());
            usuarioExistente.setNumeroCelular(usuarioActualizado.getNumeroCelular());
            usuarioExistente.setCorreo(usuarioActualizado.getCorreo());
            usuarioExistente.setContraseña(passwordEncoderPort.encodePassword(usuarioActualizado.getContraseña()));

            usuarioRepository.save(usuarioExistente);
        } else {
            throw new UnauthorizedException();
        }
    }


    /**
     * Elimina un usuario por su identificador único,
     * el director solo podra eliminar docentes.
     *
     * @param id - Identificador único del usuario a eliminar
     * @throws NoDataFoundException - Se lanza si no se encuentran datos.
     * @throws UnauthorizedException - Se lanza si no tiene permisos para acceder a este recurso.
     */
    @Override
    public void deleteUser(Long id) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        Usuario usuarioAutenticado = getUserByCorreo(correoUsuarioAutenticado);

        usuarioRepository.findById(id).orElseThrow(NoDataFoundException::new);

        if (usuarioAutenticado.getRole().getNombre().equals("ROLE_ADMIN")) {
            usuarioRepository.deleteById(id);
            return;
        }

        if (usuarioAutenticado.getId().equals(id)) {
            usuarioRepository.deleteById(id);
            return;
        }

        if (usuarioAutenticado.getRole().getNombre().equals("ROLE_DIRECTOR")) {
            Usuario usuarioAEliminar = usuarioRepository.findById(id)
                    .orElseThrow(NoDataFoundException::new);
            if (usuarioAEliminar.getRole().getNombre().equals("ROLE_DOCENTE")) {
                usuarioRepository.deleteById(id);
                return;
            }
        }
        throw new UnauthorizedException();
    }

    private String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof BearerTokenAuthentication) {
            BearerTokenAuthentication bearerTokenAuthentication = (BearerTokenAuthentication) authentication;
            Object email = bearerTokenAuthentication.getTokenAttributes().get("email");
            if (email instanceof String) {
                return (String) email;
            } else throw new RuntimeException("Error obteniendo el correo del token.");
        }
        return null;
    }
}
