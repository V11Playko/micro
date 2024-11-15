package com.micro.demo.configuration.initializacion;


import com.micro.demo.entities.Role;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IRoleRepository;
import com.micro.demo.repository.IUsuarioRepository;
import com.micro.demo.service.IAuthPasswordEncoderPort;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {
    private final IUsuarioRepository userRepository;
    private final IAuthPasswordEncoderPort passwordEncoder;
    private final IRoleRepository roleRepository;

    public DatabaseInitializer(IUsuarioRepository userRepository, IAuthPasswordEncoderPort passwordEncoder, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    public void initialize() {
        initializeRoles();
        initializeAdminUser();
    }

    private void initializeRoles() {
        createRoleIfNotExists("ROLE_ADMIN", "ROLE_ADMIN");
        createRoleIfNotExists("ROLE_DIRECTOR", "ROLE_DIRECTOR");
        createRoleIfNotExists("ROLE_DOCENTE", "ROLE_DOCENTE");
    }

    private void createRoleIfNotExists(String name, String description) {
        Role role = roleRepository.findByNombre(name);
        if (role == null) {
            role = new Role();
            role.setNombre(name);
            role.setDescripcion(description);
            roleRepository.save(role);
        }
    }

    private void initializeAdminUser() {
        if (userRepository.findByCorreo("edu.ufps10@gmail.com") == null) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setApellido("AdminSurname");
            admin.setNumeroCelular("+57 3136824595");
            admin.setCorreo("edu.ufps10@gmail.com");
            admin.setContraseña(passwordEncoder.encodePassword("admin"));

            Role adminRole = roleRepository.findByNombre("ROLE_ADMIN");
            admin.setRole(adminRole);

            userRepository.save(admin);
        }
    }
}

