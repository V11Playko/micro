package com.micro.demo.service.impl;

import com.micro.demo.configuration.security.userDetails.CustomUserDetails;
import com.micro.demo.entities.Role;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IRoleRepository;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IAuthPasswordEncoderPort;
import com.micro.demo.service.IUsuarioService;
import com.micro.demo.service.exceptions.NoDataFoundException;
import com.micro.demo.service.exceptions.NotFoundUserUnauthorized;
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

    @Override
    public List<Usuario> getAllUsers(int pagina, int elementosXpagina) {
        if (pagina < 1) {
            throw new IllegalArgumentException("El número de página debe ser mayor o igual a 1.");
        }

        Pageable pageable = PageRequest.of(pagina - 1, elementosXpagina, Sort.by("id").ascending());
        Page<Usuario> page = usuarioRepository.findAll(pageable);

        if (page.isEmpty()) {
            throw new NoDataFoundException();
        }

        return page.getContent();
    }

    @Override
    public Usuario getUserByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

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

    @Override
    public void updateUser(Long id, Usuario usuarioActualizado) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(NoDataFoundException::new);

        if (usuarioExistente != null && (correoUsuarioAutenticado != null && (correoUsuarioAutenticado.equals("admin@gmail.com") || correoUsuarioAutenticado.equals(usuarioActualizado.getCorreo())))) {
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

    @Override
    public void deleteUser(Long id) {
        String correoUsuarioAutenticado = getCorreoUsuarioAutenticado();
        if (correoUsuarioAutenticado != null || correoUsuarioAutenticado.equals("admin@gmail.com")) {
            Usuario usuarioExistente = usuarioRepository.findById(id).orElseThrow(NoDataFoundException::new);
            if (usuarioExistente != null || usuarioExistente.getCorreo().equals(correoUsuarioAutenticado)) {
                usuarioRepository.deleteById(id);
            } else {
                throw new UnauthorizedException();
            }
        } else {
            throw new NotFoundUserUnauthorized();
        }
    }

    private String getCorreoUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            return ((CustomUserDetails) authentication.getPrincipal()).getUsername();
        }
        return null;
    }
}
