package com.micro.demo.service;

import com.micro.demo.entities.Usuario;

import java.util.List;

public interface IUsuarioService {
    List<Usuario> getAllUsers(int pagina, int elementosXpagina);
    Usuario getUserByCorreo(String correo);
    void saveUser(Usuario usuario, String roleName);
    void updateUser(Long id, Usuario usuario);
    void deleteUser(Long id);

}
