package com.micro.demo.mapper;

import com.micro.demo.controller.dto.UsuarioDto;
import com.micro.demo.entities.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(source = "roleId", target = "role.id")
    Usuario toEntity(UsuarioDto temaDto);
}
