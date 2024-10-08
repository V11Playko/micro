package com.micro.demo.service;

import com.micro.demo.entities.Usuario;

import java.util.Map;

public interface IUsuarioService {
    Map<String, Object> getAllUsers(Integer pagina, Integer elementosXpagina);
    Usuario getUserByCorreo(String correo);
    Usuario getUser(Long id);
    void saveUser(Usuario usuario, String roleName);
    void updateUser(Long id, Usuario usuario);
    void deleteUser(Long id);

}
