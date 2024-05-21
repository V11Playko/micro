package com.micro.demo.configuration.security.userDetails;

import com.micro.demo.entities.Role;
import com.micro.demo.entities.Usuario;
import com.micro.demo.repository.IUsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final IUsuarioRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario user = this.userRepository.findByCorreo(email);

        List<Usuario> userEntity = userRepository.findAllById(Collections.singleton(user.getId()));

        if (userEntity.isEmpty()) {
            throw new UsernameNotFoundException("Invalid email or password");
        }

        List<Role> roles = new ArrayList<>();

        for (Usuario usuario : userEntity) {
            roles.add(user.getRole());
        }
        return CustomUserDetails.build(user, roles);
    }
}

