package com.micro.demo.service;

import com.micro.demo.controller.dto.UsuarioDto;
import com.micro.demo.entities.Usuario;

import java.util.Map;

public interface IUsuarioService {
    Map<String, Object> getAllUsers(Integer pagina, Integer elementosXpagina);
    Usuario getUserByCorreo(String correo);
    Usuario getUser(Long id);
    void saveUser(UsuarioDto usuarioDTO);
    void updateUser(Long id, UsuarioDto usuarioDTO);
    void deleteUser(Long id);

}
